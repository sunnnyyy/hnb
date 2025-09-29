package com.hnb.forms;

import com.hnb.enums.ExpenseType;

import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class ExpenseSearchForm {
    private String keyword;
    private ExpenseType expenseType;  // <-- add this field with getter/setter
    // other fields, getters/setters
}
