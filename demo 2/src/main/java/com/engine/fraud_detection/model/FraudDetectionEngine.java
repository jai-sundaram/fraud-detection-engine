package com.engine.fraud_detection.model;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayDeque;
import java.util.ArrayList;

public class FraudDetectionEngine {
    public FraudDetectionEngine(){
    }
    //velocity checks (number of transactions within a certain time frame)
    //normal - <3 in 1 minute 
    //medium risk - 3-5 in 1 minute 
    //high risk -> 5+ in 1 minute 
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

    
}
