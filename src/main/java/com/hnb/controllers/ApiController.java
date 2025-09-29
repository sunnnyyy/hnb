package com.hnb.controllers;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.hnb.entities.Customer;
import com.hnb.entities.Withdraw;
import com.hnb.forms.ExpenseSummaryDTO;
import com.hnb.forms.LastSixMonthSummaryDTO;
import com.hnb.forms.WithdrawFormDTO;
import com.hnb.repsitories.ExpenseRepo;
import com.hnb.services.CustomerService;
import com.hnb.services.ExpenseService;
import com.hnb.services.WithdrawService;

import jakarta.servlet.http.HttpSession;

@RestController
@RequestMapping("/api")
public class ApiController {

	// get contact

	@Autowired
	private CustomerService customerService;

	@Autowired
	private WithdrawService withdrawService;

	@Autowired
	private ExpenseService expenseService;

	@Autowired
	ExpenseRepo expenseRepo;

	@GetMapping("/customer/{customerId}")
	public Customer getContact(@PathVariable int customerId) {
		return customerService.getById(customerId);
	}

	@GetMapping("/get-withdrawal-amount")
	public ExpenseSummaryDTO getContact() {
		return expenseRepo.getFinancialSummary();
	}

	@GetMapping("/get-six-month-withdrawal-amount")
	public List<LastSixMonthSummaryDTO> getSummary() {
		return expenseRepo.getLastSixMonthSummary();
	}
	
	@PostMapping("submit-withdrawal")
	public ResponseEntity<String> handlePopupForm(
	        @RequestBody WithdrawFormDTO form,
	        HttpSession session
	) {
	    // --- Token Validation ---
//	    String savedToken = (String) session.getAttribute("formToken");
//	    if (savedToken == null || !savedToken.equals(form.getFormToken())) {
//	        return ResponseEntity.badRequest().body("Invalid or duplicate form submission.");
//	    }
//	    session.removeAttribute("formToken"); // Invalidate token
		
		ExpenseSummaryDTO financialSummary = expenseRepo.getFinancialSummary();
	    // --- Business Validation ---
	    double netBalance = financialSummary.getNetBalance();
	    double netCashBalance = financialSummary.getNetCashBalance();
	    double netOnlineBalance = financialSummary.getNetOnlineBalance();

	    double cash = form.getCashAmount();
	    double online = form.getOnlineAmount();
	    double total = cash + online;
	    
		if (form.getOwnerName() == null) {
			return ResponseEntity.badRequest().body("Owner name is required.");
		}
	    
		if (cash == 0 && online == 0) {
			return ResponseEntity.badRequest().body("Please enter at least one amount (Cash or Online)");
		}
		if (cash < 0 || online < 0) {
			return ResponseEntity.badRequest().body("Amounts cannot be negative.");
		}
		
	    if (cash > netCashBalance) {
	        return ResponseEntity.badRequest().body("Cash amount exceeds allowed balance (₹" + netCashBalance + ")");
	    }

	    if (online > netOnlineBalance) {
	        return ResponseEntity.badRequest().body("Online amount exceeds allowed balance (₹" + netOnlineBalance + ")");
	    }

	    if (total > netBalance) {
	        return ResponseEntity.badRequest().body("Total amount exceeds allowed balance (₹" + netBalance + ")");
	    }
	    
	  
	    try {
		    	LocalDate selectedDate = form.getWithdrawDate();
	
		    	if (selectedDate == null) {
		    	    return ResponseEntity.badRequest().body("Withdraw date is required.");
		    	}
	
		    	if (selectedDate.isAfter(LocalDate.now())) {
		    	    return ResponseEntity.badRequest().body("Withdraw date cannot be in the future.");
		    	}	    
	    	} catch (DateTimeParseException e) {
		        return ResponseEntity.badRequest().body("Invalid date format.");
		    }
	    
	    
	    // All good – process form
	    // Save to DB, etc.
	    Withdraw withdraw = new Withdraw();
	    withdraw.setOwners(form.getOwnerName());
	    withdraw.setCash(cash);
	    withdraw.setOnline(online);
	    withdraw.setTotalAmount(total);
	    withdraw.setWithdrawDate(form.getWithdrawDate());
	    withdraw.setNotes(form.getComment().trim());
//	    withdraw.setCreatedBy((String) session.getAttribute("username"));
	    withdrawService.save(withdraw);

	    return ResponseEntity.ok("Form submitted successfully!");
	}


}
