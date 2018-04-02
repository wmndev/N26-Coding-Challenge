package com.wmndev.n26.entity.model;

/**
 * Represents the transaction request's body
 */
public class Transaction{

	private double amount;
	
	private long timestamp;
	
	public Transaction(){}
	
	public Transaction(double amount, long timestamp) {
		this.amount = amount;
		this.timestamp = timestamp;
	}
	
	public long getTimestamp() {
		return timestamp;
	}
	
	public double getAmount() {
		return amount;
	}
	
	public void setAmount(double amount) {
		this.amount = amount;
	}

	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}
}
