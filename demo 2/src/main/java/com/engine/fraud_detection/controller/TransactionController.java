package com.engine.fraud_detection.controller;

import java.io.IOException;
import java.util.ArrayDeque;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.engine.fraud_detection.model.Transaction;
import com.engine.fraud_detection.service.TransactionService;

@RestController
public class TransactionController {
    private TransactionService transactionService;
    //automatically creates the transactionService and injects it into the controller 
    @Autowired
    public TransactionController(TransactionService transactionService){
        this.transactionService = transactionService;
    }
    @PostMapping("/store")
    public String storeTransaction(@RequestBody Transaction transaction) throws IOException, Exception{
        return transactionService.storeTransaction(transaction);
    }   
    @GetMapping("/search/{userId}")
    public ArrayDeque<Transaction> getAllUserTransactions(@PathVariable String userId){
        return transactionService.getAllUserTransactions(userId);
     }
}   