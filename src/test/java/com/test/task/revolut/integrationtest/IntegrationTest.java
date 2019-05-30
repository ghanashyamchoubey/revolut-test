package com.test.task.revolut.integrationtest;

import static org.junit.Assert.assertEquals;

import java.math.BigDecimal;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import com.main.task.revolut.dto.Account;
import com.main.task.revolut.dto.Transaction;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class IntegrationTest {

	private static final String Source_Acc = "12345";
	private static final String Target_Acc = "98765";
	private static final String url = "http://localhost:8080";

	private static WebTarget target;
	private static Client client;

	@BeforeClass
	public static void setUp() throws Exception {
		Thread t = new Thread(new StartServer());
		t.start();

		client = ClientBuilder.newClient();
		target = client.target(url);
	}

	@Test
	public void testCreateNewBankAccount() {
		Account detailsDTO = new Account();
		detailsDTO.setAccountId(Source_Acc);
		detailsDTO.setBalance(BigDecimal.valueOf(1000));

		Response response = target.path("/banking/createAccount").request()
				.post(Entity.entity(detailsDTO, MediaType.valueOf(MediaType.APPLICATION_JSON)));

		Account detailsDTO2 = new Account();
		detailsDTO2.setAccountId(Target_Acc);
		detailsDTO2.setBalance(BigDecimal.valueOf(2000));

		Response response2 = target.path("/banking/createAccount").request()
				.post(Entity.entity(detailsDTO2, MediaType.valueOf(MediaType.APPLICATION_JSON)));

		assertEquals(response.getStatusInfo().getStatusCode(), Response.Status.OK.getStatusCode());
		assertEquals(response2.getStatusInfo().getStatusCode(), Response.Status.OK.getStatusCode());
	}

	@Test
	public void testCreateNewBankAccountWithIncorrectURL() {
		Account detailsDTO = new Account();
		detailsDTO.setAccountId("");
		detailsDTO.setBalance(BigDecimal.valueOf(1000));

		Response response = target.path("/banking").request()
				.post(Entity.entity(detailsDTO, MediaType.valueOf(MediaType.APPLICATION_JSON)));

		assertEquals(response.getStatusInfo().getStatusCode(), Response.Status.NOT_FOUND.getStatusCode());
	}

	@Test
	public void testCreateNewBankAccountWithInvalidAccountNumber() {
		Account detailsDTO = new Account();
		detailsDTO.setAccountId("");
		detailsDTO.setBalance(BigDecimal.valueOf(1000));

		Response response = target.path("/banking/createAccount").request()
				.post(Entity.entity(detailsDTO, MediaType.valueOf(MediaType.APPLICATION_JSON)));

		assertEquals(response.getStatusInfo().getStatusCode(), Response.Status.BAD_REQUEST.getStatusCode());
	}

	@Test
	public void testCreateNewBankAccountWithInvalidAccountBalance() {
		Account detailsDTO = new Account();
		detailsDTO.setAccountId(Source_Acc);
		detailsDTO.setBalance(BigDecimal.valueOf(-1000));

		Response response = target.path("/banking/createAccount").request()
				.post(Entity.entity(detailsDTO, MediaType.valueOf(MediaType.APPLICATION_JSON)));

		assertEquals(response.getStatusInfo().getStatusCode(), Response.Status.BAD_REQUEST.getStatusCode());
	}

	@Test
	public void testCreateNewBankAccountWithNullAccountDetails() {
		Account detailsDTO = new Account();
		detailsDTO.setAccountId(null);
		detailsDTO.setBalance(null);

		Response response = target.path("/banking/createAccount").request()
				.post(Entity.entity(detailsDTO, MediaType.valueOf(MediaType.APPLICATION_JSON)));

		assertEquals(response.getStatusInfo().getStatusCode(), Response.Status.BAD_REQUEST.getStatusCode());
	}

	@Test
	public void testGetAccountById() {
		Response response = target.path("/banking/accountDetails/" + Source_Acc).request().get();
		assertEquals(response.getStatusInfo().getStatusCode(), Response.Status.OK.getStatusCode());
	}

	@Test
	public void testGetAccountByInvalidId() {
		Response response = target.path("/banking/accountDetails/55452").request().get();
		assertEquals(response.getStatusInfo().getStatusCode(), Response.Status.NOT_FOUND.getStatusCode());
	}

	@Test
	public void testTransferMoneyBetweenTwoDifferentAccounts() {
		Transaction detailsDTO = new Transaction();
		detailsDTO.setSourceAccountId(Source_Acc);
		detailsDTO.setTargetAccountId(Target_Acc);
		detailsDTO.setAmount(BigDecimal.valueOf(500));

		Response response = target.path("/banking/transact").request()
				.post(Entity.entity(detailsDTO, MediaType.valueOf(MediaType.APPLICATION_JSON)));
		assertEquals(response.getStatusInfo().getStatusCode(), Response.Status.OK.getStatusCode());
	}

	@Test
	public void testTransferMoneyBetweenTwoSameAccounts() {
		Transaction detailsDTO = new Transaction();
		detailsDTO.setSourceAccountId(Source_Acc);
		detailsDTO.setTargetAccountId(Source_Acc);
		detailsDTO.setAmount(BigDecimal.valueOf(500));

		Response response = target.path("/banking/transact").request()
				.post(Entity.entity(detailsDTO, MediaType.valueOf(MediaType.APPLICATION_JSON)));
		assertEquals(response.getStatusInfo().getStatusCode(), Response.Status.BAD_REQUEST.getStatusCode());
	}

	@Test
	public void testTransferMoneyMoreThanAccountBalance() {
		Transaction detailsDTO = new Transaction();
		detailsDTO.setSourceAccountId(Source_Acc);
		detailsDTO.setTargetAccountId(Target_Acc);
		detailsDTO.setAmount(BigDecimal.valueOf(5000));

		Response response = target.path("/banking/transact").request()
				.post(Entity.entity(detailsDTO, MediaType.valueOf(MediaType.APPLICATION_JSON)));
		assertEquals(response.getStatusInfo().getStatusCode(), Response.Status.BAD_REQUEST.getStatusCode());
	}

	@Test
	public void testTransferMoneyToNonExistingAccount() {
		Transaction detailsDTO = new Transaction();
		detailsDTO.setSourceAccountId(Source_Acc);
		detailsDTO.setTargetAccountId("78564");
		detailsDTO.setAmount(BigDecimal.valueOf(500));

		Response response = target.path("/banking/transact").request()
				.post(Entity.entity(detailsDTO, MediaType.valueOf(MediaType.APPLICATION_JSON)));
		assertEquals(response.getStatusInfo().getStatusCode(), Response.Status.BAD_REQUEST.getStatusCode());
	}
}
