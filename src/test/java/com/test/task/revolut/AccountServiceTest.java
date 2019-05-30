package com.test.task.revolut;

import static org.junit.Assert.assertEquals;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import com.main.task.revolut.db.H2Datasource;
import com.main.task.revolut.dto.Account;
import com.main.task.revolut.exception.AccountDetailsException;
import com.main.task.revolut.serviceImpl.AccountServiceImpl;
import com.main.task.revolut.util.SQLQueryUtil;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ H2Datasource.class, SQLQueryUtil.class })
public class AccountServiceTest {

	private Connection connection;
	private PreparedStatement preparedStatement;
	private ResultSet resultSet;
	private AccountServiceImpl accountServiceImpl;

	@Before()
	public void setup() throws SQLException {
		connection = Mockito.mock(Connection.class);
		preparedStatement = Mockito.mock(PreparedStatement.class);
		resultSet = Mockito.mock(ResultSet.class);
		PowerMockito.mockStatic(H2Datasource.class);
		PowerMockito.mockStatic(SQLQueryUtil.class);
		accountServiceImpl = new AccountServiceImpl();

		Mockito.when(connection.prepareStatement(Mockito.anyString())).thenReturn(preparedStatement);
		Mockito.when(preparedStatement.executeQuery()).thenReturn(resultSet);
		Mockito.when(preparedStatement.executeUpdate()).thenReturn(1);
		Mockito.when(resultSet.next()).thenReturn(true).thenReturn(false);
	}

	@Test
	public void testCreateNewAccount() throws AccountDetailsException, SQLException {
		Account detailsDTO = new Account();
		detailsDTO.setAccountId("99999");
		detailsDTO.setBalance(BigDecimal.valueOf(1000L));
		Mockito.when(H2Datasource.getConnection()).thenReturn(connection);
		Mockito.when(SQLQueryUtil.extractAccountDetailsFromResultSet(resultSet)).thenReturn(null);

		accountServiceImpl.createAccount(detailsDTO);
	}
	
	@Test(expected = AccountDetailsException.class)
	public void testExistingAccountCaseCreateNewAccount() throws AccountDetailsException, SQLException {
		Account detailsDTO = new Account();
		detailsDTO.setAccountId("99999");
		detailsDTO.setBalance(BigDecimal.valueOf(1000L));
		Mockito.when(H2Datasource.getConnection()).thenReturn(connection);
		Mockito.when(SQLQueryUtil.extractAccountDetailsFromResultSet(resultSet)).thenReturn(detailsDTO);

		accountServiceImpl.createAccount(detailsDTO);
	}

	@Test(expected = AccountDetailsException.class)
	public void testCreateNewAccountFail() throws SQLException, AccountDetailsException {
		Account detailsDTO = new Account();
		detailsDTO.setAccountId("99999");
		detailsDTO.setBalance(BigDecimal.valueOf(1000L));
		Mockito.when(H2Datasource.getConnection()).thenReturn(connection).thenThrow(SQLException.class);
		Mockito.when(SQLQueryUtil.extractAccountDetailsFromResultSet(resultSet)).thenReturn(null);

		accountServiceImpl.createAccount(detailsDTO);
	}

	@Test(expected = AccountDetailsException.class)
	public void testCreateNewAccountEmptyAccountNumber() throws AccountDetailsException, SQLException {
		Account detailsDTO = new Account();
		detailsDTO.setAccountId("");
		detailsDTO.setBalance(BigDecimal.valueOf(1000L));

		Mockito.when(H2Datasource.getConnection()).thenReturn(connection);
		Mockito.when(SQLQueryUtil.extractAccountDetailsFromResultSet(resultSet)).thenReturn(null);

		accountServiceImpl.createAccount(detailsDTO);

	}

	@Test(expected = AccountDetailsException.class)
	public void testCreateNewAccountInvalidAccountBalance() throws AccountDetailsException, SQLException {
		Account detailsDTO = new Account();
		detailsDTO.setAccountId("78965");
		detailsDTO.setBalance(BigDecimal.valueOf(-1000L));

		Mockito.when(H2Datasource.getConnection()).thenReturn(connection);
		Mockito.when(SQLQueryUtil.extractAccountDetailsFromResultSet(resultSet)).thenReturn(null);

		accountServiceImpl.createAccount(detailsDTO);
	}

	@Test(expected = AccountDetailsException.class)
	public void testCreateNewAccountInvalidAccountNumber() throws AccountDetailsException, SQLException {
		Account detailsDTO = new Account();
		detailsDTO.setAccountId("0");
		detailsDTO.setBalance(BigDecimal.valueOf(1000L));

		Mockito.when(H2Datasource.getConnection()).thenReturn(connection);
		Mockito.when(SQLQueryUtil.extractAccountDetailsFromResultSet(resultSet)).thenReturn(null);

		accountServiceImpl.createAccount(detailsDTO);
	}

	@Test(expected = AccountDetailsException.class)
	public void testCreateNewAccountNullAccountBalance() throws AccountDetailsException, SQLException {
		Account detailsDTO = new Account();
		detailsDTO.setAccountId("12345");
		detailsDTO.setBalance(null);

		Mockito.when(H2Datasource.getConnection()).thenReturn(connection);
		Mockito.when(SQLQueryUtil.extractAccountDetailsFromResultSet(resultSet)).thenReturn(null);

		accountServiceImpl.createAccount(detailsDTO);

	}

	@Test(expected = AccountDetailsException.class)
	public void testCreateNewAccountNullAccountNumber() throws AccountDetailsException, SQLException {
		Account detailsDTO = new Account();
		detailsDTO.setAccountId(null);
		detailsDTO.setBalance(BigDecimal.valueOf(1000L));

		Mockito.when(H2Datasource.getConnection()).thenReturn(connection);
		Mockito.when(SQLQueryUtil.extractAccountDetailsFromResultSet(resultSet)).thenReturn(null);

		accountServiceImpl.createAccount(detailsDTO);

	}

	@Test(expected = AccountDetailsException.class)
	public void testCreateNewAccountForExistingAccountNumber() throws AccountDetailsException, SQLException {
		Account detailsDTO = new Account();
		detailsDTO.setAccountId("1234");
		detailsDTO.setBalance(BigDecimal.valueOf(1000L));

		Mockito.when(H2Datasource.getConnection()).thenReturn(connection);
		Mockito.when(SQLQueryUtil.extractAccountDetailsFromResultSet(resultSet)).thenReturn(detailsDTO);

		accountServiceImpl.createAccount(detailsDTO);

	}

	@Test
	public void testGetAccountByNumber() throws AccountDetailsException, SQLException {
		Account detailsDTO = new Account();
		detailsDTO.setAccountId("12345");
		detailsDTO.setBalance(BigDecimal.valueOf(1000L));

		Account retVal = null;

		Mockito.when(H2Datasource.getConnection()).thenReturn(connection);
		Mockito.when(SQLQueryUtil.extractAccountDetailsFromResultSet(resultSet)).thenReturn(detailsDTO);

		retVal = accountServiceImpl.getAccountByNumber(detailsDTO.getAccountId());

		assertEquals(retVal.getAccountId(), detailsDTO.getAccountId());
	}

	@Test(expected = AccountDetailsException.class)
	public void testGetAccountByNumberFail() throws AccountDetailsException, SQLException {
		Account detailsDTO = new Account();
		detailsDTO.setAccountId("12345");
		detailsDTO.setBalance(BigDecimal.valueOf(1000L));

		Mockito.when(H2Datasource.getConnection()).thenThrow(SQLException.class);
		Mockito.when(SQLQueryUtil.extractAccountDetailsFromResultSet(resultSet)).thenReturn(detailsDTO);

		accountServiceImpl.getAccountByNumber(detailsDTO.getAccountId());

	}

}
