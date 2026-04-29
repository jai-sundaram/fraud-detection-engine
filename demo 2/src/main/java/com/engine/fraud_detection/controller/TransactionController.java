package com.engine.fraud_detection.controller;

import org.springframework.beans.factory.annotation.Autowired;
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
    @PostMapping("/process")
    public void processTransaction(@RequestBody Transaction transaction){
        transactionService.processTransaction(transaction);
    }   
    
}
