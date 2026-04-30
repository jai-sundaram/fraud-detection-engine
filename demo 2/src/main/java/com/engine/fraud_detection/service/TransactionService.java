// Velocity check (number of transactions made within a specific time frame) 
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

import org.springframework.stereotype.Service;

import com.engine.fraud_detection.model.FraudDetectionEngine;
import com.engine.fraud_detection.model.Transaction;

import java.util.HashMap;
import java.util.Map;
@Service
public class TransactionService {
    private Map<String, ArrayDeque<Transaction>> allTransactions = new HashMap<>();
    private ArrayList<String[]> merchants;
    private FraudDetectionEngine engine = new FraudDetectionEngine();
    public TransactionService(){
    }
    public void storeTransaction(Transaction transaction){
        String userId = transaction.getUserId();
        //if the userId is not already in the map, add it with an empty list of transactions 
        allTransactions.putIfAbsent(userId, new ArrayDeque<>());
        //add the transaction to the user's list of transactions
        allTransactions.get(userId).addLast(transaction);
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

    public ArrayDeque<Transaction> getTransactionByUserId(String userId){
        return allTransactions.get(userId);
    }
    public String  processTransaction(Transaction transaction){
        String userId = transaction.getUserId();
        ArrayDeque<Transaction> userTransactions = allTransactions.get(userId);
        return engine.velocityCheck(transaction, userTransactions);
    }
    
}
