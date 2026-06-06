package com.engine.fraud_detection.model;
import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
//Redis does not have to be implemented here, we are not fetching any data, just implementing the logic 
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.stereotype.Component;


@Component 
public class FraudDetectionEngine {
    //velocity checks (number of transactions within a certain time frame)
    //normal - <3 in 1 minute 
    //medium risk - 3-5 in 1 minute 
    //high risk -> 5+ in 1 minute 
    public double velocityCheck(Transaction transaction, ArrayDeque<Transaction> userTransactions){
        ArrayList<Transaction> transactionsInTheLastMinute = new ArrayList<>();
        LocalDateTime curr = transaction.getTimeStamp();
        for (Transaction t: userTransactions){
            if (Duration.between(t.getTimeStamp(), curr).getSeconds()<=60){
                transactionsInTheLastMinute.add(t);
            }
        }
        if (transactionsInTheLastMinute.size()<3){
            return 0; //normal
        }
        else if (transactionsInTheLastMinute.size() <5){
            return 5.00; //medium risk
        }
        else {
            return 10.00; //high risk
        }
    }
    public double calculateDistance(String city1, String city2) throws IOException, InterruptedException{
        //Nominatim rates limits - 1 request per second 
            //city1
            String encodedCity1 = URLEncoder.encode(city1, StandardCharsets.UTF_8);
            String url = "https://nominatim.openstreetmap.org/search"
                   + "?q=" + encodedCity1
                   + "&format=json"
                   + "&limit=1";
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("User-Agent", "fraud-detection-engine/1.0 (je.sundaram@gmail.com)") // Required by Nominatim
                .GET()
                .build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
                if (response.statusCode() != 200) {
    throw new IOException("Nominatim request failed: " + response.statusCode() + " " + response.body());
}
            if (response.statusCode() != 200) {
            throw new IOException("Nominatim request failed: " + response.statusCode() + " " + response.body());
        }

            String body = response.body().trim();

            if (!body.startsWith("[")) {
                throw new IOException("Expected JSON array from Nominatim, got: " + body);
            }

            JSONArray results = new JSONArray(body);
            JSONObject location = results.getJSONObject(0);
            double lat1 = Double.parseDouble(location.getString("lat"));
            double lon1 = Double.parseDouble(location.getString("lon"));
            //city2
            String encodedCity2 = URLEncoder.encode(city2, StandardCharsets.UTF_8);
            String url2 = "https://nominatim.openstreetmap.org/search"
                   + "?q=" + encodedCity2
                   + "&format=json"
                   + "&limit=1";
            HttpClient client2 = HttpClient.newHttpClient();
            HttpRequest request2 = HttpRequest.newBuilder()
                .uri(URI.create(url2))
                .header("User-Agent", "fraud-detection-engine/1.0 (je.sundaram@gmail.com)") // Required by Nominatim
                .GET()
                .build();
            HttpResponse<String> response2 = client2.send(request2, HttpResponse.BodyHandlers.ofString());
            String body2 = response2.body();
            JSONArray results2 = new JSONArray(body2);
            JSONObject location2 = results2.getJSONObject(0);
            double lat2 = Double.parseDouble(location2.getString("lat"));
            double lon2 = Double.parseDouble(location2.getString("lon"));
            double latDistance = Math.toRadians(lat2-lat1);
            double lonDistance = Math.toRadians(lon2 - lon1);
            lat1 = Math.toRadians(lat1);
            lat2 = Math.toRadians(lat2);
            double a = Math.pow(Math.sin(latDistance/2),2) + Math.cos(lat1) * Math.cos(lat2) * Math.pow(Math.sin(lonDistance/2), 2);
            double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
            return c * 3958.8;

    }
    public double geoVelocityCheck(Transaction transaction, ArrayDeque<Transaction> userTransactions) throws IOException, InterruptedException{
        String currLocation = transaction.getLocation();
        String lastLocation = userTransactions.peekLast().getLocation();
        //find the distance in miles 
        double distance = this.calculateDistance(currLocation, lastLocation);
        //find the time difference in hours 
        LocalDateTime currTime = transaction.getTimeStamp();
        LocalDateTime lastTime = userTransactions.peekLast().getTimeStamp();
        double timeDifference = Math.abs(Duration.between(lastTime, currTime).toSeconds()) / 3600.0;
        if (timeDifference == 0){
            //different location but at the same time 
            if(distance > 0){
                return 10.00; //high risk 
            }
            //same place at the same time, normal
            else{
                return 0.00; //normal 
            }
        }
        double speed = distance / timeDifference;
        if (speed <=80){
            return 0.00; //normal 
        }
        else if (speed <=300){
            return 5.00; //medium risk
        }
        else{
            return 10.00; //high risk
        }

    }
    //for amount anomaly, we will use z-score 
    //the z-score tells you how many standard deviations a point is from the average 
    //this can help tell us how unusual a transaciton amount is comapred to normal 
    public double amountAnomalyCheck(Transaction transaction, ArrayDeque<Transaction> userTransactions){
        ArrayList<Double> userAmounts = new ArrayList<>();
        double currAmt = transaction.getAmount();
        for (Transaction t: userTransactions){
            userAmounts.add((double) t.getAmount());
        }
        //calculating the mean
        double total = 0;
        int count = 0;
        for (double amt: userAmounts){
            total += amt;
            count += 1;
        }
        double mean = total / count;
        //calculating the standard deviation
        double newSum = 0;
        for (double amt: userAmounts){
            newSum += Math.pow(amt-mean, 2);
        }
        double stddev = Math.sqrt(newSum / count);
        //calculating the z-score 
        double z_score = (currAmt - mean)/stddev;
        //z score <=2, normal 
        //zscore 2 < x <= 3, medium risk
        //zscore > 3, high risk 
        if(z_score <=2){
            return 0.00; //normal
        }
        else if (z_score <3){
            return 5.00; //medium risk
        }
        else{
            return 10.00; //high risk
        }
    }
    public double merchantCheck(Transaction transaction, ArrayDeque<Transaction>userTransactions){
        //no need to track the actual merchant (Costco vs Kroger) bc too noisy 
        //tracking categories - (Retail, Crypto, ) - use MCC (Merchant Category Codes )
        //https://www.linkedin.com/pulse/mcc-codes-high-risk-low-risk-%D0%B8-middle-risk-businesses-alex-d/
        //https://zenpayments.com/blog/high-risk-mcc-codes/
        //gambling, financial services,tourism and travel, health and beauty, alcohol and tobacco products, various online stores 
        //im using a set because it has a contains method/O(1) search 
        Set<Integer> high_risk = Set.of(77995, 7999, 6012, 6051, 6211, 6540, 3000, 3001, 3050, 3351, 3352, 3501, 4112, 4722, 5047, 5976, 7277, 5921, 5922, 5971, 5993, 4814, 4816, 4899, 7994, 7996, 7997);
        //retail trade in food and beverages, entartainment, automotive parts and services, various services such as work of lawyers and realtors 
        Set<Integer> medium_risk = Set.of(5411, 5422, 5441, 5451, 7832, 7922, 7929, 5531, 5532, 7538, 7392, 8111, 8999);
        //if high risk -> x amt 
        //if medium risk -> y amt 
        //if normal -> z amt 
        //if seen not seen before, multiply by 1.5 
        Set<Integer> merchants = new HashSet<>();
        if (userTransactions.size() != 0){
            for(Transaction t: userTransactions){
                merchants.add(t.getMerchantCategory());
            }
        }
        int category = transaction.getMerchantCategory();
        double risk = 0;
        if (high_risk.contains(category)){
            risk = 10; //high risk
        }
        else if (medium_risk.contains(category)){
            risk = 5; //medium risk
        }
        else{
            risk = 1; //low risk 
        }
        if (merchants.size() != 0 && merchants.contains(category) == false){
            //multiply by 1.5 if user never made transaction in this category 
            risk *= 1.5;
        }

        return risk;
    }
    //Medium risk if transaction is between 12AM and 5AM, otherwise normal 
    public double timeCheck(Transaction transaction){
        int hour = transaction.getTimeStamp().getHour();
        if (hour >= 0 && hour < 5){
            return 5.00; //medium risk
        }
        else{
            return 0.00; //normal 
        }
    }

    
}
