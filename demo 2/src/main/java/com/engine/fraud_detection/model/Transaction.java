package com.engine.fraud_detection.model;
import java.time.LocalDateTime;

public class Transaction {
    private String userId; 
    private int amount;     
    private String location;
    private LocalDateTime timestamp;
    private int merchant_category;
    public Transaction() {

    }
    public Transaction(String userId, int amount, String location, LocalDateTime timestamp, int merchant_category){
        this.userId = userId;
        this.amount = amount; 
        this.location = location;
        this.timestamp = timestamp;
        this.merchant_category = merchant_category;
    }
    public String getUserId() {
        return userId;
    }
    public void setUserId(String userId) {
        this.userId = userId;
    }
    public int getAmount() {
        return amount;} 
    public void setAmount(int amount){
        this.amount = amount;
    }
    public String getLocation(){
        return location; }
    public void setLocation(String location){
        this.location = location;
    }
    public LocalDateTime getTimeStamp(){
        return timestamp;
    }
    public void setTimeStamp(LocalDateTime timestamp){
        this.timestamp = timestamp;
    }
    public int getMerchantCategory(){
        return merchant_category;
    }
    public void setMerchantCategory(int merchant_category){
        this.merchant_category = merchant_category;
    }
    public String toString(){
        return "Transaction{userId='" + userId + "', amount=" + amount + ", location='" + location + "', timestamp=" + timestamp + ", merchant_category='" + merchant_category + "'}";
    }
}

