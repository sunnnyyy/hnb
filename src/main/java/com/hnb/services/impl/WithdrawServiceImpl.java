package com.hnb.services.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.hnb.entities.Expense;
import com.hnb.entities.Withdraw;
import com.hnb.repsitories.WithdrawRepo;
import com.hnb.services.WithdrawService;

@Service
public class WithdrawServiceImpl implements WithdrawService {

	@Autowired
	WithdrawRepo withdrawRepo;
	

    @Override
    public Withdraw save(Withdraw withdraw) {
      
        return withdrawRepo.save(withdraw);
    }
    
	
    @Override
    public Page<Withdraw> getAll(int page, int size, String sortBy, String direction) {
    	
    	Sort sort = direction.equals("desc") ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
    	
    	var pageable = PageRequest.of(page, size, sort);
    	
    return withdrawRepo.findAll(pageable);  
    	
    }
   

	
}
