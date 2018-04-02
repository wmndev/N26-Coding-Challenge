package com.wmndev.n26.entity;

import java.util.List;

import com.wmndev.n26.entity.model.Transaction;
import com.wmndev.n26.exception.TransactionNotInRangeException;

public interface TransactionsContainer {
	
    /**
     * Adding transaction to container
     * @param transaction the transaction to add
     */
    void addTransaction(Transaction transaction, long timestamp) throws TransactionNotInRangeException;
    
    /**
     * provide valid transactions from container for given timestamp
     * @param timestamp the timestamp
     * @return list of valid TransactionStatisticsAggregator from container
     */
   List<TransactionStatisticsAggregator> getValidTransactionStatisticsAggregators(long timestamp);
   
   /**
    * Clear container
    */
   void clear();
    
}
