package com.engine.fraud_detection.model;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDateTime;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class TransactionTest {
    private Transaction transaction1; 
    @BeforeEach()
    public void setUp() {
        transaction1 = new Transaction("1", 100.00, "San Francisco", LocalDateTime.of(2026, 5, 22, 14, 30, 45), 5422);
    }
    @Test
    public void testGetUserId(){
        assertEquals("1", transaction1.getUserId());
    }
    @Test()
    public void testSetUserId(){
        transaction1.setUserId("2");
        assertEquals("2", transaction1.getUserId());
    }
    @Test()
    public void testGetAmount(){
        assertEquals(100.00, transaction1.getAmount());
    }
    @Test()
    public void testSetAmount(){
        transaction1.setAmount(150.00);
        assertEquals(150.00, transaction1.getAmount());
    }
    @Test()
    public void testGetLocation(){
        assertEquals("San Francisco", transaction1.getLocation());
    }
    @Test()
    public void testSetLocation(){
        transaction1.setLocation("Austin");
        assertEquals("Austin", transaction1.getLocation());
    }
    @Test()
    public void testGetTimeStamp(){
        assertEquals(LocalDateTime.of(2026, 5, 22, 14, 30, 45), transaction1.getTimeStamp());
    }
    @Test()
    public void testSetTimeStamp(){
        transaction1.setTimeStamp(LocalDateTime.of(2025, 4, 21, 9, 20, 35));
        assertEquals(LocalDateTime.of(2025, 4, 21, 9, 20, 35), transaction1.getTimeStamp());
    }
    @Test()
    public void testGetMerchantCategory(){
        assertEquals(5422, transaction1.getMerchantCategory());
    }
    @Test()
    public void testSetMerchantCategory(){
        transaction1.setMerchantCategory(5441);
        assertEquals(5441, transaction1.getMerchantCategory());
    }


}
    

