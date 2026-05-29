package com.engine.fraud_detection.model;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDateTime;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class TransactionTest {
    private Transaction transaction1; 
    @BeforeEach()
    void setUp() {
        transaction1 = new Transaction("1", 100.00, "San Francisco", LocalDateTime.of(2026, 5, 22, 14, 30, 45), 5422);
    }
    @Test
    void testGetUserId(){
        assertEquals("1", transaction1.getUserId());
    }
    @Test()
    void testSetUserId(){
        transaction1.setUserId("2");
        assertEquals("2", transaction1.getUserId());
    }
    @Test()
    void testGetAmount(){
        assertEquals(100.00, transaction1.getAmount());
    }
    @Test()
    void testSetAmount(){
        transaction1.setAmount(150.00);
        assertEquals(150.00, transaction1.getAmount());
    }
    @Test()
    void testGetLocation(){
        assertEquals("San Francisco", transaction1.getLocation());
    }
    @Test()
    void testSetLocation(){
        transaction1.setLocation("Austin");
        assertEquals("Austin", transaction1.getLocation());
    }
    @Test()
    void testGetTimeStamp(){
        assertEquals(LocalDateTime.of(2026, 5, 22, 14, 30, 45), transaction1.getTimeStamp());
    }
    @Test()
    void testSetTimeStamp(){
        transaction1.setTimeStamp(LocalDateTime.of(2025, 4, 21, 9, 20, 35));
        assertEquals(LocalDateTime.of(2025, 4, 21, 9, 20, 35), transaction1.getTimeStamp());
    }
    @Test()
    void testGetMerchantCategory(){
        assertEquals(5422, transaction1.getMerchantCategory());
    }
    @Test()
    void testSetMerchantCategory(){
        transaction1.setMerchantCategory(5441);
        assertEquals(5441, transaction1.getMerchantCategory());
    }
    @Test()
    void testEquals(){
        Transaction transaction2 = new Transaction("1", 100.00, "San Francisco", LocalDateTime.of(2026, 5, 22, 14, 30, 45), 5422);
         Transaction transaction3 = new Transaction("1", 100.00, "San Francisco", LocalDateTime.of(2026, 5, 22, 14, 30, 45), 5422);
        Transaction transaction4 = new Transaction("2", 300.00, "San Ramon", LocalDateTime.of(2026, 5, 22, 14, 30, 45), 5422);
        assertEquals(true, transaction2.equals(transaction3));
        assertEquals(false, transaction3.equals(transaction4));
    }


}
    

