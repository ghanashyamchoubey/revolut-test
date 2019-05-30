package com.main.task.revolut.service;

import com.main.task.revolut.dto.Account;
import com.main.task.revolut.exception.AccountDetailsException;

public interface AccountService {
	public void createAccount(Account account) throws AccountDetailsException;

	public Account getAccountByNumber(String accountNumber) throws AccountDetailsException;

}
