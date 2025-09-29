package com.hnb.services;

import org.springframework.data.domain.Page;

import com.hnb.entities.Expense;
import com.hnb.enums.ExpenseType;

public interface ExpenseService {
    // save contacts
	Expense save(Expense expense);

    // update contact
	//Customer update(Customer contact);

     // get contact by id

	Expense getById(int id);

    // delete contact

    void delete(int id);
    
    Page<Expense> getAll(int page, int size, String sortField, String sortDirection);

    Page<Expense> searchExpenses(String keyword, ExpenseType expenseType, int page, int size, String sortBy, String sortDirection);
    Page<Expense> searchExpenses(String keyword, int page, int size, String sortBy, String sortDirection);
   
  

}
