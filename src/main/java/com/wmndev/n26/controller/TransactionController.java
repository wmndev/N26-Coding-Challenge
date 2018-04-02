package com.wmndev.n26.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.wmndev.n26.entity.model.Transaction;
import com.wmndev.n26.exception.TransactionNotInRangeException;
import com.wmndev.n26.service.TransactionService;

@RestController
public class TransactionController {
	
	@Autowired
	private TransactionService transactionService;
	
	private static final HttpStatus STATUS_SUCCESS = HttpStatus.CREATED;
	
	private static final HttpStatus STATUS_NOT_IN_RANGE_TRANSACTION = HttpStatus.NO_CONTENT; 
	
	@PostMapping("/transactions")
	public ResponseEntity<?> addTransaction(@RequestBody Transaction transaction){
		try{
			transactionService.addTransaction(transaction);
			return new ResponseEntity<>(STATUS_SUCCESS);
		}catch(TransactionNotInRangeException e){
			return new ResponseEntity<>(STATUS_NOT_IN_RANGE_TRANSACTION);
		}
	}
}
