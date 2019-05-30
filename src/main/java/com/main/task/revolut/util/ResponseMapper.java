package com.main.task.revolut.util;

import java.io.Serializable;
import java.util.HashMap;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.main.task.revolut.dto.Account;

@JsonInclude(Include.NON_NULL)
public class ResponseMapper implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -713379003863600876L;

	protected final HashMap<String, String> payload;
	public static final String MESSAGE = "message";

	private Account accountDetails;

	public ResponseMapper(String message, Integer httpStatus) {
		payload = new HashMap<>();
		payload.put(MESSAGE, message);
	}

	public ResponseMapper() {
		payload = new HashMap<>();
	}

	public Account getAccountDetails() {
		return accountDetails;
	}

	public void setAccountDetails(Account accountDetails) {
		this.accountDetails = accountDetails;
	}

	public String getMessage() {
		return payload.get(MESSAGE);
	}

	public void setMessage(String message) {
		payload.put(MESSAGE, message);
	}
}
