package com.engine.fraud_detection.controller;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.engine.fraud_detection.model.Transaction;
import com.engine.fraud_detection.service.TransactionService;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;


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
}
