package com.wmndev.n26.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.wmndev.n26.entity.model.TransactionStatistics;
import com.wmndev.n26.service.TransactionStatisticsService;

@RestController
public class StatisticsController {
	
	private final TransactionStatisticsService transactionStatisticsService;
	
	@Autowired
	public StatisticsController(final TransactionStatisticsService transactionStatisticsService){
		this.transactionStatisticsService = transactionStatisticsService;
	}
	
	@GetMapping("/statistics")
	public TransactionStatistics getStatistics(){
		return transactionStatisticsService.produce();
	}

}
