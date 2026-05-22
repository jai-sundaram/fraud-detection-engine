package com.engine.fraud_detection.model;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDateTime;
import java.util.ArrayDeque;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.opencagedata.jopencage.JOpenCageException;
@SpringBootTest
public class EngineModelTest {
    @Autowired()
    private FraudDetectionEngine engine;
    @Test()
    public void testVelocityCheck(){
        Transaction transaction1 = new Transaction("1", 100.00, "San Francisco", LocalDateTime.of(2026, 5, 22, 14, 32, 45), 5422);
        Transaction transaction2 = new Transaction("2", 50.00, "San Francisco", LocalDateTime.of(2026, 5, 22, 14, 35, 30), 5422);
        Transaction transaction3 = new Transaction("3", 75.00, "San Francisco", LocalDateTime.of(2026, 5, 22, 14, 40, 5), 5422);
        ArrayDeque<Transaction> userTransactions = new ArrayDeque<>();
        userTransactions.add(transaction1);
        userTransactions.add(transaction2);
        assertEquals(0.00, engine.velocityCheck(transaction3, userTransactions));
        userTransactions.add(transaction3);
        Transaction transaction4 = new Transaction("4", 75.00, "San Francisco", LocalDateTime.of(2026, 5, 22, 14, 40, 10), 5422);
        userTransactions.add(transaction4);
        Transaction transaction5 = new Transaction("5", 75.00, "San Francisco", LocalDateTime.of(2026, 5, 22, 14, 40, 15), 5422);
        userTransactions.add(transaction5);
        Transaction transaction6 = new Transaction("6", 75.00, "San Francisco", LocalDateTime.of(2026, 5, 22, 14, 40, 20), 5422);
        assertEquals(5.00, engine.velocityCheck(transaction6, userTransactions));
        userTransactions.add(transaction6);
        Transaction transaction7 = new Transaction("7", 75.00, "San Francisco", LocalDateTime.of(2026, 5, 22, 14, 40, 40), 5422);
        userTransactions.add(transaction7);
        Transaction transaction8 = new Transaction("8", 75.00, "San Francisco", LocalDateTime.of(2026, 5, 22, 14, 40, 54), 5422);
        assertEquals(10.00, engine.velocityCheck(transaction8, userTransactions));
    }
    @Test()
    public void testGeoVelocityCheck() throws JOpenCageException{
        ArrayDeque<Transaction> userTransactions = new ArrayDeque<>();
        Transaction transaction1 = new Transaction("1", 100.00, "San Francisco, California", LocalDateTime.of(2026, 5, 22, 14, 32, 45), 5422);
        userTransactions.add(transaction1);
        Transaction transaction2 = new Transaction("2", 100.00, "New York, New York", LocalDateTime.of(2026, 5, 22, 14, 35, 20), 5422);
        assertEquals(10.00, engine.geoVelocityCheck(transaction2, userTransactions));
        Transaction transaction3 = new Transaction("3", 100.00, "Daly City, California", LocalDateTime.of(2026, 5, 22, 15, 45, 20), 5422);
        assertEquals(5.00, engine.geoVelocityCheck(transaction3, userTransactions));
        Transaction transaction4 = new Transaction("4", 100.00, "San Ramon, California", LocalDateTime.of(2026, 6, 23, 20, 45, 20), 5422);
        assertEquals(0.00, engine.geoVelocityCheck(transaction4, userTransactions));
        Transaction transaction5 = new Transaction("5", 100.00, "Boston, Massachusetts", LocalDateTime.of(2026, 5, 25, 14, 32, 45), 5422);
        assertEquals(0.00, engine.geoVelocityCheck(transaction5, userTransactions));
    }
    @Test()
    public void amountAnomalyCheckTest(){
        ArrayDeque<Transaction> userTransactions = new ArrayDeque<>();
        Transaction transaction1 = new Transaction("1", 100.00, "San Francisco, California", LocalDateTime.of(2026, 5, 22, 14, 32, 45), 5422);
        Transaction transaction2 = new Transaction("2", 150.00, "San Francisco, California", LocalDateTime.of(2026, 5, 22, 14, 35, 20), 5422);
        Transaction transaction3 = new Transaction("3", 250.00, "San Francisco, California", LocalDateTime.of(2026, 5, 22, 15, 45, 20), 5422);
        Transaction transaction4 = new Transaction("4", 500.00, "San Francisco, California", LocalDateTime.of(2026, 6, 23, 20, 50, 20), 5422);
        Transaction transaction5 = new Transaction("5", 500.00, "San Francisco, California", LocalDateTime.of(2026, 6, 24, 20, 50, 20), 5422);
        Transaction transaction6 = new Transaction("6", 250.00, "San Francisco, California", LocalDateTime.of(2026, 6, 24, 21, 50, 20), 5422);
        userTransactions.add(transaction1);
        userTransactions.add(transaction2);
        userTransactions.add(transaction3);
        userTransactions.add(transaction4);
        userTransactions.add(transaction5);
        userTransactions.add(transaction6);
        Transaction transaction7 = new Transaction("7", 600.00, "San Francisco, California", LocalDateTime.of(2026, 6, 26, 21, 50, 20), 5422);
        assertEquals(0.00, engine.amountAnomalyCheck(transaction7, userTransactions));
        Transaction transaction8 = new Transaction("8", 700.00, "San Francisco, California", LocalDateTime.of(2026, 7, 26, 21, 50, 20), 5422);
        assertEquals(5.00, engine.amountAnomalyCheck(transaction8, userTransactions));
        Transaction transaction9 = new Transaction("9", 1000.00, "San Francisco, California", LocalDateTime.of(2026, 8, 26, 21, 50, 20), 5422);
        assertEquals(10.00, engine.amountAnomalyCheck(transaction9, userTransactions));
    }
    
}
