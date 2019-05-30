package com.main.task.revolut.exception;

import javax.ws.rs.core.Response;

public class ValidationException extends Exception {
	/**
	 * 
	 */
	private static final long serialVersionUID = 7959050492477510853L;
	private Response.Status status;

	public ValidationException(String message, Response.Status status) {
		super(message);
		this.status = status;
	}

	public Response.Status getStatus() {
		return status;
	}
}
