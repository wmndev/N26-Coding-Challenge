package com.wmndev.n26.service;

import java.time.Instant;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.wmndev.n26.entity.TransactionsContainerImpl;
import com.wmndev.n26.entity.model.Transaction;
import com.wmndev.n26.exception.TransactionNotInRangeException;

@Service
public class TransactionServiceImpl implements TransactionService {
	
	private final TransactionsContainerImpl transactionsContainer;
	
	@Autowired
	public  TransactionServiceImpl(final TransactionsContainerImpl transactionsContainer){
		this.transactionsContainer = transactionsContainer;
	}

	@Override
	public void addTransaction(Transaction transaction) throws TransactionNotInRangeException {
		long currentTimeStamp = Instant.now().toEpochMilli();
		transactionsContainer.addTransaction(transaction, currentTimeStamp);
	}

}
