package com.wmndev.n26.service;

import java.time.Instant;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.wmndev.n26.entity.TransactionStatisticsAggregator;
import com.wmndev.n26.entity.TransactionsContainerImpl;
import com.wmndev.n26.entity.model.TransactionStatistics;

@Service
public class TransactionStatisticsServiceImpl implements TransactionStatisticsService {

	private final TransactionsContainerImpl transactionsContainer;
	
	@Autowired
	public  TransactionStatisticsServiceImpl(final TransactionsContainerImpl transactionsContainer){
		this.transactionsContainer = transactionsContainer;
	}
	
	@Override
	public TransactionStatistics produce() {
		List<TransactionStatisticsAggregator> txnStatsAggregator = getStatisticsAggregators();
		
		TransactionStatistics result = new TransactionStatistics();
		
		txnStatsAggregator.forEach(t -> t.mergeToResult(result));
		
		return result;
	}

	/**
	 * Get All relevant transaction statistics aggregtors
	 * @param timestamp the time stamp 
	 * @return a List of TransactionStatisticsAggregator
	 */
	private List<TransactionStatisticsAggregator> getStatisticsAggregators() {
		long currentTime = Instant.now().toEpochMilli();
		return transactionsContainer.getValidTransactionStatisticsAggregators(currentTime);		
	}


}
