// Velocity check (number of transactions made within a specific time frame)  - done 
// Geo-velocity fraud detection (Analyzing speed of location changes between transactions) 
// https://www.fraud.net/glossary/geo-velocity-fraud-detection#what-is-geo-velocity-fraud-detection
// Amount anomaly: The transaction amount is unusually large 
// Suspicious merchant: New or uncrecognized merchant (new merchant, new merchant category )
// Time anomaly: Transaction is made at unusual time compared to usual patterns (between 12AM- 5AM)

package com.engine.fraud_detection.service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayDeque;
import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.engine.fraud_detection.model.FraudDetectionEngine;
import com.engine.fraud_detection.model.Transaction;
import com.opencagedata.jopencage.JOpenCageException;

import java.util.HashMap;
import java.util.Map;
@Service
public class TransactionService {
    private Map<String, ArrayDeque<Transaction>> allTransactions = new HashMap<>();
    private FraudDetectionEngine engine ;
    @Autowired
    public TransactionService(FraudDetectionEngine engine){
        this.engine = engine;
    }
    public String storeTransaction(Transaction transaction) throws JOpenCageException{
        String userId = transaction.getUserId();
        //if the userId is not already in the map, add it with an empty list of transactions 
        allTransactions.putIfAbsent(userId, new ArrayDeque<>());
        //get all transactions for the user 
        ArrayDeque<Transaction> userTransactions= allTransactions.get(userId);
        double total = 0;
        if (userTransactions.size()>0){
            // System.out.println(engine.velocityCheck(transaction, userTransactions));
            // System.out.println(engine.geoVelocityCheck(transaction, userTransactions));
            // System.out.println(engine.merchantCheck(transaction, userTransactions));
            // System.out.println(engine.timeCheck(transaction));
            total = engine.velocityCheck(transaction, userTransactions) + engine.geoVelocityCheck(transaction, userTransactions) + engine.merchantCheck(transaction, userTransactions) + engine.timeCheck(transaction); }
        if(userTransactions.size()>=5){
            total += engine.amountAnomalyCheck(transaction, userTransactions);
        }
        if (userTransactions.size() == 0){
            total = 1; //the first transaction
        }
            //<5 - low risk 
            //5 - 20 medium risk
            //20+ high risk
        if (total < 5){
                allTransactions.get(userId).addLast(transaction);
                return "Low risk transaction. Transaction processed.";
            }
        else if (total < 20){
                allTransactions.get(userId).addLast(transaction);
                return "Medium risk transaction. Transaction processed.";
            }
            else{
                return "High risk transaction. Transaction declined.";
            }
        // Every single time u add a new transaction, remove transactions for this user that are older than 10 minutes 
        //first, let us get the deque of transactios for the current user 
        //When we affect this deque, we also affect the allTransactions deque, since the this gives us a reference 
        // ArrayDeque<Transaction> userTransactions= allTransactions.get(userId);
        // LocalDateTime currDate = transaction.getTimeStamp();
        // // while (Duration.between(userTransactions.peekFirst().getTimeStamp(), currDate).toMinutes()>10){
        // //     userTransactions.removeFirst();
        // // }
        // System.out.println(engine.velocityCheck(transaction, userTransactions));
    }
    public ArrayDeque<Transaction> getAllUserTransactions(String userId){
        //deque of transactions for the user 
        ArrayDeque<Transaction> userTransactions = allTransactions.get(userId);
        return userTransactions;
    }
    
}
