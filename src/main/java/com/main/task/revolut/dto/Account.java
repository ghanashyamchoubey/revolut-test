package com.main.task.revolut.dto;

import java.io.Serializable;
import java.math.BigDecimal;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class Account implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1735835151674006951L;

	public Account() {

	}

	private String accountId;

	private BigDecimal balance;

	public String getAccountId() {
		return accountId;
	}

	public void setAccountId(String accountId) {
		this.accountId = accountId;
	}

	public BigDecimal getBalance() {
		return balance;
	}

	public void setBalance(BigDecimal balance) {
		this.balance = balance;
	}

}
