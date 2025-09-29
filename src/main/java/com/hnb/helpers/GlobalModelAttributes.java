package com.hnb.helpers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import com.hnb.services.CustomerService;
import com.hnb.services.ExpenseService;

@ControllerAdvice // this applies to all controllers
public class GlobalModelAttributes {

	@Autowired
	private CustomerService customerService;


	@ModelAttribute
	public void addCustomerCount(Model model) {
		long totalCustomersThisMonth = customerService.countCustomersInCurrentMonth();
		model.addAttribute("totalCustomersThisMonth", totalCustomersThisMonth);

	}

	
}
