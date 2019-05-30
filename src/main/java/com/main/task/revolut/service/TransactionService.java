package com.main.task.revolut.service;

import com.main.task.revolut.dto.Transaction;
import com.main.task.revolut.exception.TransferException;


public interface TransactionService {
	
	public void transact(Transaction transactionDetailsDTO) throws TransferException;


}
