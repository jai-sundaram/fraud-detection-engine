// Velocity check (number of transactions made within a specific time frame) 
// Geo-velocity fraud detection (Analyzing speed of location changes between transactions) 
// https://www.fraud.net/glossary/geo-velocity-fraud-detection#what-is-geo-velocity-fraud-detection
// Amount anomaly: The transaction amount is unusually large 
// Suspicious merchant: New or uncrecognized merchant 
// Time anomaly: Transaction is made at unusual time compared to usual patterns

package com.engine.service;

import java.util.ArrayList;

import org.springframework.stereotype.Service;

import com.engine.model.Transaction;

import java.util.HashMap;
import java.util.Map;
@Service
public class TransactionService {
    private Map<String, ArrayList<Transaction>> allTransactions = new HashMap<>();
    public TransactionService(){
    }
    public void processTransaction(Transaction transaction){
        String userId = transaction.getUserId();
        //if the userId is not already in the map, add it with an empty list of transactions 
        allTransactions.putIfAbsent(userId, new ArrayList<>());
        //add the transaction to the user's list of transactions
        allTransactions.get(userId).add(transaction);
        ArrayList<Transaction> userTransactions = allTransactions.get(userId);
        System.out.println(userTransactions);
    }
    
}
