package com.engine.fraud_detection.controller;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.engine.fraud_detection.model.Transaction;
import com.engine.fraud_detection.service.TransactionService;
import com.fasterxml.jackson.databind.ObjectMapper;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
@WebMvcTest(TransactionController.class)
public class TransactionControllerTest {
    @Autowired
    private MockMvc mockMvc;
    //controller tests are spring tests, not unit testsn 
    @MockitoBean
    private TransactionService transactionService;
    @Autowired
    private ObjectMapper objectMapper;
    @Test()
    void testStoreTransaction() throws Exception{
        //mocking the return value of transactionService method
        when(transactionService.storeTransaction(any(Transaction.class)))
                .thenReturn("Low risk transaction. Transaction processed.");
        mockMvc.perform(post("/store")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                      "userId": "1",
                      "amount": 500.00,
                      "location": "San Francisco, California",
                      "timeStamp": "2026-05-22T14:32:45",
                      "merchantCategory": 5422
                    }
                """))
                .andExpect(status().isOk())
                .andExpect(content().string("Low risk transaction. Transaction processed."));
    }
    @Test()
    void getAllUserTransactions() throws Exception{
        Transaction transaction1 = new Transaction("1", 5100.00, "San Francisco, California", LocalDateTime.of(2026, 5, 22, 14, 32, 45), 5422);
        ArrayDeque<Transaction> userTransactions = new ArrayDeque<>();
        userTransactions.add(transaction1);
        when(transactionService.getAllUserTransactions("1"))
                .thenReturn(userTransactions);
        mockMvc.perform(get("/search/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].userId").value("1"))
                .andExpect(jsonPath("$[0].amount").value(5100.00))
                .andExpect(jsonPath("$[0].location").value("San Francisco, California"))
                .andExpect(jsonPath("$[0].timeStamp").value("2026-05-22T14:32:45"))
                .andExpect(jsonPath("$[0].merchantCategory").value(5422));

    }
@Test
void overallTestLatency() throws Exception {

    when(transactionService.storeTransaction(any(Transaction.class)))
            .thenReturn("Low risk transaction. Transaction processed.");

    List<Transaction> transactions = List.of(
        new Transaction("1", 45.99, "San Francisco, California",
                LocalDateTime.of(2026, 5, 22, 9, 15, 0), 5411),

        new Transaction("1", 12.50, "San Francisco, California",
                LocalDateTime.of(2026, 5, 22, 9, 15, 20), 5411),

        new Transaction("1", 87.25, "San Francisco, California",
                LocalDateTime.of(2026, 5, 22, 9, 15, 40), 5422),

        new Transaction("1", 150.00, "San Francisco, California",
                LocalDateTime.of(2026, 5, 22, 9, 16, 10), 5531),

        new Transaction("1", 72.40, "San Francisco, California",
                LocalDateTime.of(2026, 5, 22, 9, 16, 35), 5441),

        new Transaction("1", 35.00, "San Jose, California",
                LocalDateTime.of(2026, 5, 22, 9, 18, 0), 5411),

        new Transaction("1", 89.99, "San Jose, California",
                LocalDateTime.of(2026, 5, 22, 9, 20, 0), 5451),

        new Transaction("1", 110.50, "Sacramento, California",
                LocalDateTime.of(2026, 5, 22, 9, 25, 0), 5411),

        new Transaction("1", 60.75, "Sacramento, California",
                LocalDateTime.of(2026, 5, 22, 9, 30, 0), 5532),

        new Transaction("1", 99.99, "Los Angeles, California",
                LocalDateTime.of(2026, 5, 22, 10, 0, 0), 5411),

        new Transaction("1", 500.00, "Los Angeles, California",
                LocalDateTime.of(2026, 5, 22, 10, 5, 0), 7922),

        new Transaction("1", 2500.00, "Las Vegas, Nevada",
                LocalDateTime.of(2026, 5, 22, 10, 10, 0), 7999),

        new Transaction("1", 4200.00, "New York, New York",
                LocalDateTime.of(2026, 5, 22, 10, 10, 30), 6051),

        new Transaction("1", 850.00, "Chicago, Illinois",
                LocalDateTime.of(2026, 5, 22, 10, 15, 0), 6211),

        new Transaction("1", 67.30, "Chicago, Illinois",
                LocalDateTime.of(2026, 5, 22, 13, 0, 0), 5411),

        new Transaction("1", 40.25, "Chicago, Illinois",
                LocalDateTime.of(2026, 5, 22, 13, 30, 0), 5422),

        new Transaction("1", 15000.00, "Miami, Florida",
                LocalDateTime.of(2026, 5, 22, 14, 0, 0), 6540),

        new Transaction("2", 22.99, "Miami, Florida",
                LocalDateTime.of(2026, 5, 22, 14, 5, 0), 5411),

        new Transaction("1", 48.75, "Atlanta, Georgia",
                LocalDateTime.of(2026, 5, 22, 14, 30, 0), 5451),

        new Transaction("1", 31.10, "Atlanta, Georgia",
                LocalDateTime.of(2026, 5, 22, 14, 45, 0), 5411)
    );

    long start = System.nanoTime();

    for (Transaction transaction : transactions) {
        mockMvc.perform(post("/store")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(transaction)))
                .andExpect(status().isOk());
    }

    long end = System.nanoTime();
    double avgNano = (end - start) / (double) transactions.size();
    throw new Exception("Average latency: " + avgNano + " ns");

}
}