// Velocity check (number of transactions made within a specific time frame)  - done 
// Geo-velocity fraud detection (Analyzing speed of location changes between transactions) 
// https://www.fraud.net/glossary/geo-velocity-fraud-detection#what-is-geo-velocity-fraud-detection
// Amount anomaly: The transaction amount is unusually large 
// Suspicious merchant: New or uncrecognized merchant (new merchant, new merchant category )
// Time anomaly: Transaction is made at unusual time compared to usual patterns (between 12AM- 5AM)

package com.engine.fraud_detection.service;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import com.engine.fraud_detection.model.FraudDetectionEngine;
import com.engine.fraud_detection.model.Transaction;

import java.io.IOException;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
@Service
public class TransactionService {
    //redisTemplate is also key-value based just like how a HasHMap is key value based
    //IN this case, the value type is "Transaction" because each value is technically a deque that only contains transactions
    @Autowired
    private RedisTemplate <String, Transaction> allTransactions;
    @Autowired
    private FraudDetectionEngine engine ;
    public String storeTransaction(Transaction transaction) throws IOException, InterruptedException{
        String userId = transaction.getUserId();
        //fetch all transactions stored in redis for a given user/userid
        //opsForList acessed the redis list oeprations
        //range (key, start, end) -> (the key, the start index, last end). The start and end index in the list of transactions for that user. 
        List<Transaction> uTransactions = allTransactions.opsForList().range(userId, 0, -1);
        if (uTransactions == null){
            uTransactions = new ArrayList<>();
        }
        ArrayDeque<Transaction> userTransactions = new ArrayDeque<>(uTransactions);
        double total = engine.merchantCheck(transaction, userTransactions) + engine.timeCheck(transaction);
        if (userTransactions.size() > 0){
        total += engine.velocityCheck(transaction, userTransactions) + engine.geoVelocityCheck(transaction, userTransactions); }
        if(userTransactions.size()>=5){
            total += engine.amountAnomalyCheck(transaction, userTransactions);
        }
            //<5 - low risk 
            //5 - 20 medium risk
            //20+ high risk
            //when you add multiple items with the same key, a list is basically created for that key, and further entries are added to that list
            //if there is no entry with that key, it is auomtatically created
        if (total < 10){
                allTransactions.opsForList().rightPush(userId, transaction);
                return "Low risk transaction. Transaction processed.";
            }
        else if (total < 20){
                allTransactions.opsForList().rightPush(userId, transaction);
                return "Medium risk transaction. Transaction processed.";
            }
            else{
                return "High risk transaction. Transaction declined.";
            } 
        }
    public ArrayDeque<Transaction> getAllUserTransactions(String userId){
        //deque of transactions for the user 
        List<Transaction> uTransactions = allTransactions.opsForList().range(userId, 0, -1);
        ArrayDeque<Transaction> userTransactions = new ArrayDeque<>(uTransactions);
        return userTransactions;
    }
    
}
