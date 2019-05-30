package com.main.task.revolut.controller;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.main.task.revolut.dto.Account;
import com.main.task.revolut.dto.Transaction;
import com.main.task.revolut.exception.AccountDetailsException;
import com.main.task.revolut.exception.TransferException;
import com.main.task.revolut.service.AccountService;
import com.main.task.revolut.service.TransactionService;
import com.main.task.revolut.util.ResponseMapper;

@Path("/banking")
public class AccountController {

	@Inject
	private AccountService accountService;

	@Inject
	private TransactionService transactionService;

	@POST
	@Path("/createAccount")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response createAccount(Account account) {
		ResponseMapper responseMapper = new ResponseMapper();
		try {
			accountService.createAccount(account);
		} catch (AccountDetailsException e) {
			responseMapper.setMessage(e.getMessage());
			return Response.status(e.getStatus()).entity(responseMapper).build();
		}
		responseMapper.setMessage("Account Created Successfully with Id : " + account.getAccountId());
		return Response.ok(responseMapper).build();
	}

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/accountDetails/{accountNumber}")
	public Response getAccountByNumber(@PathParam(value = "accountNumber") String accountNumber) {
		ResponseMapper responseMapper = new ResponseMapper();
		Account account;
		try {
			account = accountService.getAccountByNumber(accountNumber);
			if (null == account) {
				responseMapper.setMessage("Account " + accountNumber + " does not exist.");
				return Response.status(Response.Status.NOT_FOUND).entity(responseMapper).build();
			} else {
				responseMapper.setAccountDetails(account);
			}
		} catch (AccountDetailsException e) {
			responseMapper.setMessage(e.getMessage());
			return Response.status(e.getStatus()).entity(responseMapper).build();
		}
		return Response.ok(responseMapper).build();
	}

	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	@Path("/transact")
	public Response transact(Transaction transactionDetailsDTO) {
		ResponseMapper mapper = new ResponseMapper();
		try {
			transactionService.transact(transactionDetailsDTO);
			mapper.setMessage("Transaction Successful !!");
			return Response.ok().entity(mapper).build();
		} catch (TransferException e) {
			mapper.setMessage(e.getMessage());
			return Response.status(e.getStatus()).entity(mapper).build();
		}

	}

}
