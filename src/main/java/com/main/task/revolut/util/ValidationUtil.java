package com.main.task.revolut.util;

import java.math.BigDecimal;

public class ValidationUtil {

	private ValidationUtil() {
	}

	public static boolean isValidAccountNumber(String accountNumber) {
		if (null!= accountNumber && accountNumber.matches("[0-9]+") && accountNumber.length() == 5) {
			return true;
		}
		return false;
	}
	
	public static boolean isValidAccountBalance(BigDecimal balance) {
		if(null != balance && balance.compareTo(BigDecimal.ZERO) > 0) {
			return true;
		}
		return false;
	}
}
