package com.engine.fraud_detection.model;

import java.util.ArrayList;

public class FraudDetectionEngine {
    public FraudDetectionEngine(){
    }
    //velocity checks (number of transactions within a certain time frame)
    //normal - <3 in 1 minute 
    //medium risk - 3-5 in 1 minute 
    //high risk -> 5+ in 1 minute 
    public int velocityCheck(Transaction transaction, ArrayList<Transaction> userTransactions){
        return 0;
    }

    
}
