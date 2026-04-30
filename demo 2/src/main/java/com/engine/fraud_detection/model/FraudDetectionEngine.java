package com.engine.fraud_detection.model;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayDeque;
import java.util.ArrayList;

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
    public double geoVelocityCheck(Transaction transaction, ArrayDeque<Transaction> userTransactions) throws JOpenCageException{
        String currLocation = transaction.getLocation();
        String lastLocation = userTransactions.peekLast().getLocation();
        //find the distance in miles 
        double distance = calculateDistance(currLocation, lastLocation);
        //find the time difference in hours 
        LocalDateTime currTime = transaction.getTimeStamp();
        LocalDateTime lastTime = userTransactions.peekLast().getTimeStamp();
        double timeDifference = Duration.between(lastTime, currTime).toHours();
        double speed = distance;

        return speed;
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

    
}
