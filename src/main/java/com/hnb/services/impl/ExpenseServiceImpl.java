package com.hnb.services.impl;

import java.time.LocalDate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.hnb.entities.Expense;
import com.hnb.enums.ExpenseType;
import com.hnb.helpers.ResourceNotFoundException;
import com.hnb.repsitories.ExpenseRepo;
import com.hnb.repsitories.UserRepo;
import com.hnb.services.ExpenseService;

@Service
public class ExpenseServiceImpl implements ExpenseService
{

    private final UserRepo userRepo;
    @Autowired
    private ExpenseRepo expenseRepo;


    ExpenseServiceImpl(UserRepo userRepo) {
        this.userRepo = userRepo;
    }


    @Override
    public Expense save(Expense expense) {
      
        return expenseRepo.save(expense);
    }

  

    @Override
    public Expense getById(int id) {
        return expenseRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Contact not found with given id " + id));
    }

    @Override
    public void delete(int id) {
        var contact = expenseRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Contact not found with given id " + id));
        expenseRepo.delete(contact);

    }


	
    @Override
    public Page<Expense> getAll(int page, int size, String sortBy, String direction) {
    	
    	Sort sort = direction.equals("desc") ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
    	
    	var pageable = PageRequest.of(page, size, sort);
    	
    return expenseRepo.findAll(pageable);  
    	
    }
   
    @Override
    public Page<Expense> searchExpenses(String keyword, int page, int size, String sortBy, String direction) {
	  	Sort sort = direction.equals("desc") ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
	    	var pageable = PageRequest.of(page, size, sort);
	    return expenseRepo.searchExpenses(keyword, pageable);
    }

    @Override
    public Page<Expense> searchExpenses(String keyword, ExpenseType expenseType, int page, int size, String sortBy, String direction) {
    	Sort sort = direction.equals("desc") ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
    	var pageable = PageRequest.of(page, size, sort);
    	return expenseRepo.searchExpenses(keyword, expenseType, pageable);
    }

   
    
//    @Override
//	public Expense getWithdrawalAmount() {
//		Expense expense = expenseRepo.getWithdrawalAmount();
//		return expense;
//	}


	/*
	 * @Override public Page<Customer> getByUser(User user, int page, int size,
	 * String sortBy, String direction) {
	 * 
	 * Sort sort = direction.equals("desc") ? Sort.by(sortBy).descending() :
	 * Sort.by(sortBy).ascending();
	 * 
	 * var pageable = PageRequest.of(page, size, sort);
	 * 
	 * return customerRepo.findByUser(user, pageable);
	 * 
	 * }
	 */
    
    
 
    
	 

}
