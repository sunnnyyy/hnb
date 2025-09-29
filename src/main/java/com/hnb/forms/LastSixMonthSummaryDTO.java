package com.hnb.forms;

public interface LastSixMonthSummaryDTO {
	Integer getYear();
	String getMonth();
	Double getTotalExpense();
	Double getTotalRevenue();
	Double getTotalExpenseAllMonths();
	Double getTotalRevenueAllMonths();

}
