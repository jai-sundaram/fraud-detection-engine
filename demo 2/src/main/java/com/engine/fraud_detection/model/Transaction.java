package com.engine.fraud_detection.model;
import java.time.LocalDateTime;
import java.util.Objects;

public class Transaction {
    private String userId; 
    private double amount;     
    private String location;
    private LocalDateTime timestamp;
    private int merchant_category;
    public Transaction() {

    }
    public Transaction(String userId, double amount, String location, LocalDateTime timestamp, int merchant_category){
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
    public double getAmount() {
        return amount;} 
    public void setAmount(double amount){
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
    @Override
    public boolean equals(Object other){
        if (!(other instanceof Transaction)) {
            return false;
        }
        Transaction transaction = (Transaction) other;
        return this.userId.equals(transaction.userId) && this.amount == transaction.amount && this.location.equals(transaction.location) && this.timestamp.equals(transaction.timestamp) && this.merchant_category == transaction.merchant_category;
    }
    //hashCode is used in hash-based collections like HashMap, hashSet etc
    //it is used to provide a hash code/label number for each object
    //much faster to look up an object using a hash code, dont have to check one by one 
    //Java asks for the hash code -> number tells where to look -> Use equals to check if the object is right 
    @Override
    public int hashCode(){
        return Objects.hash(userId, amount, location, timestamp, merchant_category);
    }
}

