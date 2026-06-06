package com.engine.fraud_detection;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.engine.fraud_detection.model.Transaction;
import com.engine.fraud_detection.service.TransactionService;
//CommandLineRunner is a Spring Boot interface that allows you to run this code once the whole application starts up
//when you run the application, the object will be created and the run method will be automatically called 
//Spring creats it, so use Component 
@Component
public class MetricCalculation implements CommandLineRunner {
    //doing constructor injection, rather than creatingn the object using the new keyword, it will let us get the dependencies 
    private TransactionService transactionService;
    public MetricCalculation(TransactionService transactionService){
        this.transactionService = transactionService;
    }
    @Override
    public void run(String... args) throws Exception {
        transactionService.storeTransaction(new Transaction("1", 45.99, "San Francisco, California",
            LocalDateTime.of(2026, 5, 22, 9, 15, 0), 5411));
            long start = System.nanoTime();
            transactionService.storeTransaction(new Transaction("1", 87.25, "San Francisco, California",
            LocalDateTime.of(2026, 5, 22, 9, 15, 40), 5422));
            long end = System.nanoTime();
            double elapsedNano = (end - start);
            System.out.println( "Average latency: " + elapsedNano + " nanoseconds");
        }
}
    

