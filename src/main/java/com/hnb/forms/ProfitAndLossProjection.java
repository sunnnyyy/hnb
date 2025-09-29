// com.hnb.forms.ProfitAndLossProjection.java

package com.hnb.forms;

public interface ProfitAndLossProjection {
    Integer getYear();
    String getMonth();
    Double getTotalExpense();
    Double getTotalCashExpense();
    Double getTotalOnlineExpense();
    Double getTotalCashRevenue();
    Double getTotalOnlineRevenue();
    Long getTotalCustomers();
}
