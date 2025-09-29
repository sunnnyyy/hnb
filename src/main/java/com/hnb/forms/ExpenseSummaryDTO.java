package com.hnb.forms;

public interface ExpenseSummaryDTO {
    Double getTotalExpense();
    Double getTotalCashExpense();
    Double getTotalOnlineExpense();

    Double getTotalWithdrawal();
    Double getTotalCashWithdrawal();
    Double getTotalOnlineWithdrawal();

    Double getTotalRevenue();
    Double getTotalCashRevenue();
    Double getTotalOnlineRevenue();

    // You can compute derived values (like net balance) in Java
    default Double getTotalOutflow() {
        return getTotalExpense() + getTotalWithdrawal();
    }

    default Double getTotalInflow() {
        return getTotalCashRevenue() + getTotalOnlineRevenue();
    }

    default Double getNetBalance() {
        return getTotalInflow() - getTotalOutflow();
    }

	default Double getNetCashBalance() {
		return getTotalCashRevenue() - (getTotalCashExpense() + getTotalCashWithdrawal());
	}

	default Double getNetOnlineBalance() {
		return getTotalOnlineRevenue() - (getTotalOnlineExpense() + getTotalOnlineWithdrawal());
	}
}
