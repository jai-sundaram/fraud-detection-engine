package com.model;

import java.time.LocalDateTime;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity 
public class Transaction {
    @Id
    private String userId; 
    private int amount;     
    private String location;
    private LocalDateTime timestamp;
    private String merchant; 
    public Transaction() {

    }
    public Transaction(String userId, int amount, String location, LocalDateTime timestamp, String merchant){
        this.userId = userId;
        this.amount = amount; 
        this.location = location;
        this.timestamp = timestamp;
        this.merchant = merchant; 
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
    public String getMerchant(){
        return merchant; 
    }
    public void setMerchant(String merchant){
        this.merchant = merchant;
    }
}

