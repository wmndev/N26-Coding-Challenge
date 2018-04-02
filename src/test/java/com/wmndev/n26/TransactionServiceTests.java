package com.wmndev.n26;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.time.Instant;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.IntStream;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.wmndev.n26.entity.TransactionStatisticsAggregator;
import com.wmndev.n26.entity.TransactionsContainer;
import com.wmndev.n26.entity.model.Transaction;
import com.wmndev.n26.exception.TransactionNotInRangeException;
import com.wmndev.n26.service.TransactionService;


@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = N26Application.class)
public class TransactionServiceTests {
	
	@Autowired
	private TransactionService transactionService;
	
	@Autowired
	private TransactionsContainer container;
	
	@Before
	public void before(){
		container.clear();
	}
	
	@Test(expected=TransactionNotInRangeException.class )
	public void testGetExceptionWhenInputHasInvalidTimestamp() throws TransactionNotInRangeException{
		Transaction txn = new Transaction(12.5, 123);
		transactionService.addTransaction(txn);
	}
	
	@Test
	public void testEmptyGettingAggregatorsWithInValidTime() {
		long time = Instant.now().toEpochMilli(); 
		Transaction txn = new Transaction(12.5, 123);
		try {
			transactionService.addTransaction(txn);
		} catch (TransactionNotInRangeException e) {}
		
		List<TransactionStatisticsAggregator> list = container.getValidTransactionStatisticsAggregators(time);
	
		assertNotNull(list);
		assertEquals(0, list.size());
	}
	
	@Test
	public void testConcurrentTransactions(){
		final ExecutorService executor = Executors.newFixedThreadPool(10);
		long time = Instant.now().toEpochMilli(); 
		try{	 
			 IntStream.range(0, 100).forEach(i-> { 
				 executor.execute(()->{
					 Transaction t = new Transaction(15 * i, time - (i + i * 100) );
					 try {
						 Thread.sleep(1); //making concurrent more realistic
						transactionService.addTransaction(t);
					} catch (Exception e) {}
				 });
				 
			 });
		 
		}finally{
			 executor.shutdown();
		}
		
		try {
			Thread.sleep(2000); //making sure all completed
		} catch (InterruptedException e) {}
		
		List<TransactionStatisticsAggregator> list = container.getValidTransactionStatisticsAggregators(time);
		
		assertNotNull(list);
		
		int sum = 0;
		for (TransactionStatisticsAggregator agg : list){
			sum += agg.getTransactionStatistics().getCount();
			
		}
		
		assertEquals(100, sum);	
	}
	
	

	
	
	
	
	

}
