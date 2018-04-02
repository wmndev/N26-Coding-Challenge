package com.wmndev.n26.entity;

import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import com.wmndev.n26.entity.model.Transaction;
import com.wmndev.n26.entity.model.TransactionStatistics;

public class TransactionStatisticsAggregator {
	
	private ReadWriteLock lock;
	
	private TransactionStatistics transactionStatistics;
	
	private long timestamp;
	
	public TransactionStatisticsAggregator(){
		transactionStatistics = new TransactionStatistics();
		this.lock = new ReentrantReadWriteLock();
	}

	public ReadWriteLock getLock() {
		return lock;
	}
	
	public void create(Transaction transaction){
		transactionStatistics.setMin(transaction.getAmount());
		transactionStatistics.setMax(transaction.getAmount());
		transactionStatistics.setCount(1);
		transactionStatistics.setAvg(transaction.getAmount());
		transactionStatistics.setSum(transaction.getAmount());
		timestamp = transaction.getTimestamp();
	}
	
	/**
	 * merging statistics together
	 * @param result the result to return
	 * @param other the iterating TransactionStatisticsAggregator
	 */
	public void mergeToResult(TransactionStatistics result) {
		try{
			getLock().readLock().lock();
			
			result.setSum(result.getSum() + getTransactionStatistics().getSum());
			result.setCount(result.getCount() + getTransactionStatistics().getCount());
			result.setAvg(result.getSum() / result.getCount());
			
			if (result.getMin() > getTransactionStatistics().getMin()){
				result.setMin(getTransactionStatistics().getMin());
			}
			if (result.getMax() < getTransactionStatistics().getMax()){
				result.setMax(getTransactionStatistics().getMax());
			}	
		}finally {
			getLock().readLock().unlock();
		}
	}
	
	
	
	
	/**
	 * merging new transaction with existing transaction statistics
	 * @param transaction
	 */
	public void merge(Transaction transaction) {
		transactionStatistics.setSum(transactionStatistics.getSum() + transaction.getAmount());
		transactionStatistics.setCount(transactionStatistics.getCount() + 1);
		transactionStatistics.setAvg(transactionStatistics.getSum() / transactionStatistics.getCount());
		
		if (transactionStatistics.getMin() > transaction.getAmount()){
			transactionStatistics.setMin(transaction.getAmount());
		}
		if (transactionStatistics.getMax() < transaction.getAmount()){
			transactionStatistics.setMax(transaction.getAmount());
		}
		
	}
	
	public boolean isEmpty(){
		return transactionStatistics.getCount() == 0;
	}
	
	public long getTimestamp(){
		return timestamp;
	}
	
	public TransactionStatistics getTransactionStatistics(){
		return transactionStatistics;
	}

	public void reset() {
		transactionStatistics.reset();	
		timestamp = 0;
	}

	
}
