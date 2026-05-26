package com.engine.fraud_detection.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.ArrayDeque;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.engine.fraud_detection.model.FraudDetectionEngine;
import com.engine.fraud_detection.model.Transaction;
import com.opencagedata.jopencage.JOpenCageException;
//mockito extension 
@ExtendWith(MockitoExtension.class)
public class TransactionServiceTest {
    private TransactionService transactionService;
    //we want to mock the engine, only testing the transactionService not the engine 
    @Mock()
    private FraudDetectionEngine engine;
    @BeforeEach()
    void setUp(){
        transactionService = new TransactionService(engine);
    }
    @Test()
    void testStoreTransaction() throws JOpenCageException{
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
    void testGetAllUserTransactions() throws JOpenCageException{
        Transaction transaction1 = new Transaction("1", 5100.00, "San Francisco, California", LocalDateTime.of(2026, 5, 22, 14, 32, 45), 5422);
        transactionService.storeTransaction(transaction1);
        ArrayDeque<Transaction> userTransaction1 = new ArrayDeque<>();
        userTransaction1.add(transaction1);
        assertIterableEquals(userTransaction1, transactionService.getAllUserTransactions("1"));
    }
    @Test
    void latencyTest() throws JOpenCageException {

        Transaction transaction = new Transaction(
            "1",
            100.00,
            "San Francisco, California",
            LocalDateTime.now(),
            5422
        );

        long start = System.nanoTime();

        transactionService.storeTransaction(transaction);

        long end = System.nanoTime();

        double elapsedMs = (end - start);

         //   throw new RuntimeException( "Average latency: " + elapsedMs + " nanoseconds");
    }

    
}
