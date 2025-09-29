package com.hnb.controllers;


import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.hnb.entities.Expense;
import com.hnb.entities.User;
import com.hnb.entities.Withdraw;
import com.hnb.forms.ExpenseForm;
import com.hnb.forms.ExpenseSearchForm;
import com.hnb.forms.ExpenseSummaryDTO;
import com.hnb.forms.ProfitAndLossProjection;
import com.hnb.helpers.AppConstants;
import com.hnb.helpers.Helper;
import com.hnb.helpers.Message;
import com.hnb.helpers.MessageType;
import com.hnb.repsitories.ExpenseRepo;
import com.hnb.services.ExpenseService;
import com.hnb.services.UserService;
import com.hnb.services.WithdrawService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

@Controller
@RequestMapping("/user/expense")
public class ExpenseController {

	private Logger logger = org.slf4j.LoggerFactory.getLogger(ExpenseController.class);

	
	@Autowired
	private UserService userService;
	
	
	@Autowired
	private ExpenseService expenseService;
	
	@Autowired
	private WithdrawService withdrawService;
	
	@Autowired
	private ExpenseRepo expenseRepo;
	
    private static final String UPLOAD_DIR = "uploads/expenseDocuments/";
    private static final String[] ALLOWED_EXTENSIONS = {"jpg", "png", "pdf"};

	
	   @RequestMapping("/add")
		public String addExpenseView(Model model) {
			
		   ExpenseForm expenseForm = new ExpenseForm();
		   model.addAttribute("expenseForm", expenseForm);
		   return "user/add_expense";
		}

	    @RequestMapping(value = "/add", method = RequestMethod.POST)
	    public String saveExpense(@Valid @ModelAttribute ExpenseForm expenseForm,
	                              BindingResult result,
	                              Authentication authentication,
	                              HttpSession session,
	                              Model model,
	                              HttpServletRequest request,
	                              @RequestParam("billUrl") MultipartFile file                      
	    		) {

	        // 1. Validate form
	        if (result.hasErrors()) {
	            result.getAllErrors().forEach(error -> logger.info(error.toString()));
	            session.setAttribute("message",
	                    Message.builder()
	                            .content("Please correct the following errors")
	                            .type(MessageType.red)
	                            .build());
	            return "user/add_expense";
	        }

	        try {
        	
		        	String originalFilename = StringUtils.cleanPath(file.getOriginalFilename());
		        	String fileExtension = getFileExtension(originalFilename);
	
		        	// Validate extension
		        	if (!originalFilename.isBlank() && !isAllowedExtension(fileExtension)) {
		        		session.setAttribute("message",
		        				Message.builder()
		        				.content("Allowned file types: jpg, png, pdf")
		        				.type(MessageType.red)
		        				.build());
		        		return "user/add_expense";
		        	}
		        	
	
		        	Expense expense = new Expense();
		        	if (!originalFilename.isBlank()) {
			        	String savedFileName = System.currentTimeMillis()/1000000 + "_" + file.getOriginalFilename();
			        	Path filePath = Paths.get(UPLOAD_DIR, savedFileName);
			        	Files.createDirectories(filePath.getParent());  // Ensure directory exists
			        	Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
			        	expense.setBillUrl(savedFileName);
		        	}
	    	            // 2. Get logged-in user
	            String username = Helper.getEmailOfLoggedInUser(authentication);
	            User user = userService.getUserByEmail(username);

	            // 3. Create Customer entity
	            expense.setExpenseType(expenseForm.getExpenseType());
	            expense.setAmount(expenseForm.getAmount());
	            expense.setNotes(expenseForm.getNotes());
	            expense.setExpenseDate(expenseForm.getExpenseDate());
	            expense.setPaymentMode(expenseForm.getPaymentMode());
	            expense.setCreatedBy(user.getUserId());

	            // 4. Save customer first (to generate ID)
	            Expense savedCustomer = expenseService.save(expense);
     
	            session.setAttribute("message",
	                    Message.builder()
	                            .content("You have successfully added a new expense")
	                            .type(MessageType.green)
	                            .build());

	            return "redirect:/user/expense/add";

        } catch (IOException e) {
            logger.error("File upload failed", e);
            session.setAttribute("message",
                    Message.builder()
                            .content("Failed to upload documents")
                            .type(MessageType.red)
                            .build());
            return "user/add_expense";   
        } catch (Exception ex) {
	            logger.error("Failed to save expense", ex);
	            session.setAttribute("message",
	                    Message.builder()
	                            .content("An unexpected error occurred")
	                            .type(MessageType.red)
	                            .build());
	            return "user/add_expense";
	        }
	    }


		// view contacts

		@RequestMapping
		public String viewExpense(@RequestParam(value = "page", defaultValue = "0") int page,
				@RequestParam(value = "size", defaultValue = AppConstants.PAGE_SIZE + "") int size,
				@RequestParam(value = "sortBy", defaultValue = "expenseDate") String sortBy,
				@RequestParam(value = "direction", defaultValue = "desc") String direction, Model model,
				Authentication authentication) {

			// load all the user contacts
			String username = Helper.getEmailOfLoggedInUser(authentication);


			Page<Expense> pageExpense =
					expenseService.getAll(page, size, sortBy, direction);
			

			model.addAttribute("pageExpense", pageExpense);
			model.addAttribute("pageSize", AppConstants.PAGE_SIZE);

			model.addAttribute("expenseSearchForm", new ExpenseSearchForm());

			return "user/expense";
		}


		@RequestMapping("/search")
		public String searchHandler(

				@ModelAttribute ExpenseSearchForm expenseSearchForm,
				@RequestParam(value = "size", defaultValue = AppConstants.PAGE_SIZE + "") int size,
				@RequestParam(value = "page", defaultValue = "0") int page,
				@RequestParam(value = "sortBy", defaultValue = "expenseDate") String sortBy,
				@RequestParam(value = "direction", defaultValue = "desc") String direction, Model model,
				Authentication authentication) {

//			logger.info("field {} keyword {}", customerSearchForm.getField(), customerSearchForm.getValue());
			var user = userService.getUserByEmail(Helper.getEmailOfLoggedInUser(authentication));

//			System.out.println("field " + expenseSearchForm.getKeyword() + " keyword " + expenseSearchForm.getExpenseType() + "size "+ size);
//			Page<Expense> pageExpense = expenseService.searchExpenses(customerSearchForm.getValue(), page, size, sortBy, direction);
			Page<Expense> pageExpense = expenseService.searchExpenses(expenseSearchForm.getKeyword(),expenseSearchForm.getExpenseType(), page, size, sortBy, direction);

			logger.info("pageExpense {}", pageExpense);

			model.addAttribute("expenseSearchForm", expenseSearchForm);

			model.addAttribute("pageExpense", pageExpense);

			model.addAttribute("pageSize", AppConstants.PAGE_SIZE);

			return "user/expense";
		}
		
		@RequestMapping("/report")
		public String viewExpenseReport(
				@ModelAttribute ExpenseSearchForm expenseSearchForm,
				@RequestParam(defaultValue = "0") int page,
				@RequestParam(defaultValue = "24") int size, Model model) {
	
			Pageable pageable = PageRequest.of(page, size);
	        Page<ProfitAndLossProjection> pageReport = expenseRepo.getProfitAndLossReport(pageable);
//	        System.out.println("pageReport-------->>>> "+pageReport);
//	        System.out.println("Total Pages: " + pageReport.getTotalPages());
//	        System.out.println("Current Page: " + pageReport.getNumber());
//	        System.out.println("Content Size: " + pageReport.getContent().size());

	        model.addAttribute("pageReport", pageReport);
	        model.addAttribute("currentPage", page);
	        model.addAttribute("totalPages", pageReport.getTotalPages());
	        model.addAttribute("pageSize", size);
			model.addAttribute("expenseSearchForm", expenseSearchForm);

			return "user/report_expense";
		}
		
		
		@RequestMapping("/report/owner")
		public String viewExpenseRevenueReport(
						@RequestParam(value = "size", defaultValue = AppConstants.PAGE_SIZE + "") int size,
						@RequestParam(value = "page", defaultValue = "0") int page,
						@RequestParam(value = "sortBy", defaultValue = "withdrawDate") String sortBy,
						@RequestParam(value = "direction", defaultValue = "desc") String direction, Model model,
						Authentication authentication) {

					Page<Withdraw> pageReport = withdrawService.getAll(page, size, sortBy, direction);					
					ExpenseSummaryDTO financialSummary = expenseRepo.getFinancialSummary();
					List<Map<String, Object>> ownerAmount = expenseRepo.getOwnerWithdrawMoney();
					long totalSum = ownerAmount.stream()
						    .mapToLong(row -> {
						        Object val = row.get("total");
						        if (val instanceof Number) {
						            return ((Number) val).longValue();
						        }
						        return 0L;
						    })
						    .sum();


					model.addAttribute("totalSum", totalSum);
					model.addAttribute("pageReport", pageReport);
					model.addAttribute("financialSummary", financialSummary);
					model.addAttribute("ownerAmount", ownerAmount);

					model.addAttribute("pageSize", AppConstants.PAGE_SIZE);

					return "user/report_owner_expense";
		}
		
		
	    // Helper to get file extension
	    private String getFileExtension(String filename) {
	        if (filename.contains(".")) {
	            return filename.substring(filename.lastIndexOf('.') + 1).toLowerCase();
	        }
	        return "";
	    }

	    // Helper to check allowed extension
	    private boolean isAllowedExtension(String extension) {
	        for (String allowed : ALLOWED_EXTENSIONS) {
	            if (allowed.equalsIgnoreCase(extension)) {
	                return true;
	            }
	        }
	        return false;
	    }

	
}


