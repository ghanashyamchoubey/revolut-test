package com.main.task.revolut.exception;

import javax.ws.rs.core.Response;

public class TransferException extends Exception {
	/**
	 * 
	 */
	private static final long serialVersionUID = 2161553355956049565L;
	private Response.Status status;

	public TransferException(String message, Response.Status status) {
		super(message);
		this.status = status;
	}

	public Response.Status getStatus() {
		return status;
	}
}
