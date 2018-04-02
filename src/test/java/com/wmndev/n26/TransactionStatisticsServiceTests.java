package com.wmndev.n26;

import static org.junit.Assert.assertTrue;

import java.time.Instant;
import java.util.stream.IntStream;

import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.wmndev.n26.entity.TransactionsContainer;
import com.wmndev.n26.entity.model.Transaction;
import com.wmndev.n26.entity.model.TransactionStatistics;
import com.wmndev.n26.exception.TransactionNotInRangeException;
import com.wmndev.n26.service.TransactionService;
import com.wmndev.n26.service.TransactionStatisticsService;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = N26Application.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class TransactionStatisticsServiceTests {
	
	@Autowired
	private TransactionStatisticsService transactionStatsService;
	
	@Autowired
	private TransactionService transactionService;
	
	@Autowired
	private TransactionsContainer container;
	
	@Before
	public void before(){
		container.clear();
	}
	
	@Test
	public void test1_ServiceReturnEmptyResult(){
		TransactionStatistics stats = transactionStatsService.produce();
		
		assertTrue(stats.getSum() == 0);
		assertTrue(stats.getCount() == 0);
		
	}
	
	@Test
	public void test2_ServiceReturnNotEmptyResult(){
		long time = Instant.now().toEpochMilli();
		int[] sum = new int[]{0};
		IntStream.range(-1, 99).forEach(i->{
			Transaction t = new Transaction(i,time - i * 100 );
			sum[0] += i;
			try {
				transactionService.addTransaction(t);
			} catch (TransactionNotInRangeException e) {}
		});
		
		TransactionStatistics stats = transactionStatsService.produce();
		
		assertTrue(stats.getSum() == sum[0]);
		assertTrue(stats.getCount() == 100);
		assertTrue(stats.getMin() == -1);
		assertTrue(stats.getMax() == 98);
	}
	
	

}
