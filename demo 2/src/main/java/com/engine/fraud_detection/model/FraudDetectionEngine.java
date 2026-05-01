package com.engine.fraud_detection.model;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.opencagedata.jopencage.JOpenCageException;
import com.opencagedata.jopencage.JOpenCageGeocoder;
import com.opencagedata.jopencage.model.JOpenCageForwardRequest;
import com.opencagedata.jopencage.model.JOpenCageLatLng;
import com.opencagedata.jopencage.model.JOpenCageResponse;
@Component 
public class FraudDetectionEngine {
    public FraudDetectionEngine(){
    }
    //velocity checks (number of transactions within a certain time frame)
    //normal - <3 in 1 minute 
    //medium risk - 3-5 in 1 minute 
    //high risk -> 5+ in 1 minute 
    @Value("${geocoding.api.key}")
    private String key; 
    public String velocityCheck(Transaction transaction, ArrayDeque<Transaction> userTransactions){
        ArrayList<Transaction> transactionsInTheLastMinute = new ArrayList<>();
        LocalDateTime curr = transaction.getTimeStamp();
        for (Transaction t: userTransactions){
            if (Duration.between(t.getTimeStamp(), curr).getSeconds()<=60){
                transactionsInTheLastMinute.add(t);
            }
        }
        if (transactionsInTheLastMinute.size()<3){
            return "normal";
        }
        else if (transactionsInTheLastMinute.size() <5){
            return "medium risk";
        }
        else {
            return "high risk";
        }
    }
    public String geoVelocityCheck(Transaction transaction, ArrayDeque<Transaction> userTransactions) throws JOpenCageException{
        String currLocation = transaction.getLocation();
        String lastLocation = userTransactions.peekLast().getLocation();
        //find the distance in miles 
        double distance = calculateDistance(currLocation, lastLocation);
        //find the time difference in hours 
        LocalDateTime currTime = transaction.getTimeStamp();
        LocalDateTime lastTime = userTransactions.peekLast().getTimeStamp();
        double timeDifference = Duration.between(lastTime, currTime).toHours();
        double speed = distance / timeDifference;
        if (speed <=80){
            return "normal";
        }
        else if (speed <=300){
            return "medium risk";
        }
        else{
            return "high risk";
        }

    }
    //haversine formula to calculate distance between two cities (using lat/long coordinates)
    //https://www.baeldung.com/java-find-distance-between-points
    private double calculateDistance(String city1, String city2) throws JOpenCageException{
        JOpenCageGeocoder jOpenCageGeocoder = new JOpenCageGeocoder(this.key);
        JOpenCageForwardRequest request1 = new JOpenCageForwardRequest(city1);
        JOpenCageResponse response1 = jOpenCageGeocoder.forward(request1);
        JOpenCageLatLng latLng1 = response1.getFirstPosition();
        double lat1 = latLng1.getLat();
        double lon1 = latLng1.getLng();
        JOpenCageForwardRequest request2 = new JOpenCageForwardRequest(city2);
        JOpenCageResponse response2= jOpenCageGeocoder.forward(request2);
        JOpenCageLatLng latLng2 = response2.getFirstPosition();
        double lat2 = latLng2.getLat();
        double lon2 = latLng2.getLng();
        double latDistance = Math.toRadians(lat2-lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);
        lat1 = Math.toRadians(lat1);
        lat2 = Math.toRadians(lat2);
        double a = Math.pow(Math.sin(latDistance/2),2) + Math.cos(lat1) * Math.cos(lat2) * Math.pow(Math.sin(lonDistance/2), 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
        return c * 3958.8;
    }
    //for amount anomaly, we will use z-score 
    //the z-score tells you how many standard deviations a point is from the average 
    //this can help tell us how unusual a transaciton amount is comapred to normal 
    public String amountAnomalyCheck(Transaction transaction, ArrayDeque<Transaction> userTransactions){
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
            return "normal";
        }
        else if (z_score <3){
            return "medium risk";
        }
        else{
            return "high risk"; 
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
        for(Transaction t: userTransactions){
            merchants.add(t.getMerchantCategory());
        }
        int category = transaction.getMerchantCategory();
        double risk = 0;
        if (high_risk.contains(category)){
            risk = 20; //or some other number idk 
        }
        else if (medium_risk.contains(category)){
            risk = 10; //or other 
        }
        else{
            risk = 5; // or other 
        }
        if (merchants.contains(category) == false){
            risk *= 1.5;
        }

        return risk;
    }

    
}
