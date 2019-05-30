package com.main.task.revolut.exception;

import javax.ws.rs.core.Response;

public class AccountDetailsException extends Exception {
	/**
	 * 
	 */
	private static final long serialVersionUID = 8039346291420510883L;
	private Response.Status status;

	public AccountDetailsException(String message, Response.Status status) {
		super(message);
		this.status = status;
	}

	public Response.Status getStatus() {
		return status;
	}

}
