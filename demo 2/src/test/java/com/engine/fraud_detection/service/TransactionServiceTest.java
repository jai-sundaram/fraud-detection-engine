package com.engine.fraud_detection.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayDeque;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import com.engine.fraud_detection.model.FraudDetectionEngine;
import com.engine.fraud_detection.model.Transaction;
@SuppressWarnings("unchecked")
@SpringBootTest
public class TransactionServiceTest {
    @Autowired()
    private TransactionService transactionService;
    //we want to mock the engine, only testing the transactionService not the engine 
    //redisTemplate is more like a client/conneciton object to redis, not like a data strucucture 
    //since it is basically like an object, it needs to be autowired 
    @Autowired()
    private RedisTemplate<String, Transaction> redisTemplate;
    @MockitoBean()
    private FraudDetectionEngine engine;
    //redis is a db that persists data, so we need to wipe it clean before each test to avoid problems 
    //runs before each test method 
    @BeforeEach
    void setUp() {
        redisTemplate.delete("1");
    }
    @Test()
    void testStoreTransaction() throws IOException, InterruptedException{
        Transaction transaction1 = new Transaction("1", 5100.00, "San Francisco, California", LocalDateTime.of(2026, 5, 22, 14, 32, 45), 5422);
        assertEquals("Low risk transaction. Transaction processed.", transactionService.storeTransaction(transaction1));
        ArrayDeque<Transaction> userTransaction1 = new ArrayDeque<>();
        userTransaction1.add(transaction1);
        assertIterableEquals(userTransaction1, transactionService.getAllUserTransactions("1"));
        Transaction transaction2 = new Transaction("1", 5000.00, "New York, New York", LocalDateTime.of(2026, 5, 22, 14, 32, 45), 7999);
        //mock return values for the engine methods 
        when(engine.geoVelocityCheck(any(Transaction.class), any(ArrayDeque.class)))
        .thenReturn(10.0);
        when(engine.merchantCheck(any(Transaction.class), any(ArrayDeque.class)))
        .thenReturn(15.0);
        when(engine.velocityCheck(any(Transaction.class), any(ArrayDeque.class)))
        .thenReturn(0.0);
        when(engine.timeCheck(any(Transaction.class)))
        .thenReturn(0.0);
        assertEquals("High risk transaction. Transaction declined.", transactionService.storeTransaction(transaction2));
        Transaction transaction3 = new Transaction("1", 5000.00, "New York, New York", LocalDateTime.of(2026, 5, 23, 0, 35, 45), 7999);
        when(engine.geoVelocityCheck(any(Transaction.class), any(ArrayDeque.class)))
        .thenReturn(0.00);
        when(engine.merchantCheck(any(Transaction.class), any(ArrayDeque.class)))
        .thenReturn(10.0);
        when(engine.velocityCheck(any(Transaction.class), any(ArrayDeque.class)))
        .thenReturn(0.0);
        when(engine.timeCheck(any(Transaction.class)))
        .thenReturn(5.00);
        assertEquals("Medium risk transaction. Transaction processed.", transactionService.storeTransaction(transaction3));
        Transaction transaction4 = new Transaction("1", 4500.00, "New York, New York", LocalDateTime.of(2026, 5, 23, 0, 50, 45), 7999);
        transactionService.storeTransaction(transaction4);
        Transaction transaction5 = new Transaction("1", 4500.00, "New York, New York", LocalDateTime.of(2026, 5, 23, 0, 55, 45), 7999);
        transactionService.storeTransaction(transaction5);
        Transaction transaction6 = new Transaction("1", 4500.00, "Yonkers, New York", LocalDateTime.of(2026, 5, 23, 0, 58, 45), 7999);
        transactionService.storeTransaction(transaction6);
        Transaction transaction7= new Transaction("1", 10000.00, "New York, New York", LocalDateTime.of(2026, 5, 25, 0, 58, 45), 7999);
        when(engine.geoVelocityCheck(any(Transaction.class), any(ArrayDeque.class)))
        .thenReturn(0.00);
        when(engine.merchantCheck(any(Transaction.class), any(ArrayDeque.class)))
        .thenReturn(10.0);
        when(engine.velocityCheck(any(Transaction.class), any(ArrayDeque.class)))
        .thenReturn(0.0);
        when(engine.timeCheck(any(Transaction.class)))
        .thenReturn(5.00);
        when(engine.amountAnomalyCheck(any(Transaction.class), any(ArrayDeque.class)))
        .thenReturn(10.00);
        assertEquals("High risk transaction. Transaction declined.", transactionService.storeTransaction(transaction7));

    }
    @Test()
    void testGetAllUserTransactions() throws IOException, InterruptedException{
        Transaction transaction1 = new Transaction("1", 5100.00, "San Francisco, California", LocalDateTime.of(2026, 5, 22, 14, 32, 45), 5422);
        transactionService.storeTransaction(transaction1);
        ArrayDeque<Transaction> userTransaction1 = new ArrayDeque<>();
        userTransaction1.add(transaction1);
        assertIterableEquals(userTransaction1, transactionService.getAllUserTransactions("1"));
    }

    
}