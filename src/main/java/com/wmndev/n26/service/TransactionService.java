package com.wmndev.n26.service;

import com.wmndev.n26.entity.model.Transaction;
import com.wmndev.n26.exception.TransactionNotInRangeException;

public interface TransactionService {
	
	/**
	 * Add new Transaction 
	 * @param transaction the Transaction 
	 */
	void addTransaction(Transaction transaction) throws TransactionNotInRangeException;
	
}
