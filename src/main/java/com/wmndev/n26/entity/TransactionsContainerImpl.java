package com.wmndev.n26.entity;


import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.wmndev.n26.entity.model.Transaction;
import com.wmndev.n26.exception.TransactionNotInRangeException;

/**
 * A singleton component object, representing an aggregator container for incoming transactions
 * each incoming message will be given an index in the container, then if the index is 
 * already populated with valid transaction aggregator - then the incoming transaction will be aggregated.
 * Otherwise, aggregator will be cleared and populate with incoming transaction
 */

@Component 
public class TransactionsContainerImpl implements TransactionsContainer{
	
	private TransactionStatisticsAggregator[] transactionStatisticsAggregator;
	
	//max time (in millis) from current time that transaction will be considered valid 
	@Value("${time.mills.max}")
	private int maxTimeMillsToKeep;

	//represents the time interval (e.g. 1000ms = one second)
	@Value("${time.mills.interval}")
	private int timeMillsInterval;

    private TransactionsContainerImpl () {}
    
    @PostConstruct
    private void postConstruct(){
    	if (maxTimeMillsToKeep <= 0 || timeMillsInterval <= 0) 
    		throw new IllegalArgumentException("YML is missing valid positive values for time.mills.max or time.mills.interval");
    	
    	this.transactionStatisticsAggregator = new TransactionStatisticsAggregator[maxTimeMillsToKeep / timeMillsInterval];
    	
    	initAggregator();	
    }
    
    /**
     * fill transactionStatistics with empty statistics aggregators 
     */
    private void initAggregator(){
    	//fill transactionStatistics with empty statistics aggregators
    	for (int x = 0; x < transactionStatisticsAggregator.length; x++){
    		transactionStatisticsAggregator[x] = new TransactionStatisticsAggregator();
    	}	
    }


    @Override
    public void addTransaction(Transaction transaction, long currentTimestamp) throws TransactionNotInRangeException{   	
    	if (!isTransactionValid(transaction.getTimestamp(), currentTimestamp)) 
    		throw new TransactionNotInRangeException();
    	    	
    	aggregate(transaction, currentTimestamp);	
    }
    
	@Override
	public List<TransactionStatisticsAggregator> getValidTransactionStatisticsAggregators(long currentTimestamp) {
		return Arrays.stream(transactionStatisticsAggregator)
				.filter(t -> isTransactionValid(t.getTimestamp(), currentTimestamp))
				.collect(Collectors.toList());
	}
	
	@Override
	public void clear() {
		initAggregator();		
	}


	/**
	 * Responsible for aggregate the transaction into 
	 * the Transaction Statistics Aggregator array
	 * @param transaction the transaction
	 * @param index
	 */
	private void aggregate(Transaction transaction, long currentTimestamp) {
		//getting the transaction index
		int index = getTransactionIndex(transaction);
		
		TransactionStatisticsAggregator txnStatisticAggregator = transactionStatisticsAggregator[index];

		try {
			txnStatisticAggregator.getLock().writeLock().lock();
			
			// in case aggregator is empty
			if (txnStatisticAggregator.isEmpty()) { 
				txnStatisticAggregator.create(transaction);

			} else {
				// check if existing aggregator is still valid
				if (isTransactionValid(txnStatisticAggregator.getTimestamp(), currentTimestamp)) {
					txnStatisticAggregator.merge(transaction);
				} else { //if not valid
					txnStatisticAggregator.reset(); 
					txnStatisticAggregator.create(transaction);
				}
			}

		} finally {
			txnStatisticAggregator.getLock().writeLock().unlock();
		}
	}

	/**
	 * Get the correct transaction index in the transactionStatisticsAggregator array
	 * @param transaction the transaction
	 * @return the index
	 */
	private int getTransactionIndex(Transaction transaction){
    	long txnTime = transaction.getTimestamp();
    	
    	long currTime = Instant.now().toEpochMilli();
    	
    	return (int)((currTime - txnTime) / timeMillsInterval) % (maxTimeMillsToKeep / timeMillsInterval);
    }
    

    /**
     * Check if time stamp is in range
     * @param timeStamp the txn timestamp
     * @param currentTimestamp the current timestamp
     * @return true if valid
     */
    private boolean isTransactionValid(long txnTimeStamp, long currentTimestamp){
    	return txnTimeStamp >= currentTimestamp - maxTimeMillsToKeep;
    }
}
