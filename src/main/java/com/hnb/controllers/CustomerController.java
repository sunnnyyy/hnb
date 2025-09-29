package com.hnb.controllers;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import com.hnb.entities.Customer;
import com.hnb.entities.CustomerDetails;
import com.hnb.entities.User;
import com.hnb.enums.DocumentType;
import com.hnb.forms.CustomerForm;
import com.hnb.forms.CustomerSearchForm;
import com.hnb.helpers.AppConstants;
import com.hnb.helpers.Helper;
import com.hnb.helpers.Message;
import com.hnb.helpers.MessageType;
import com.hnb.services.CustomerService;
import com.hnb.services.UserService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

@Controller
@RequestMapping("/user/customer")
public class CustomerController {

	private Logger logger = org.slf4j.LoggerFactory.getLogger(CustomerController.class);

	@Autowired
	private CustomerService customerService;

//	@Autowired
//	private ImageService imageService;

	@Autowired
	private UserService userService;
    private static final String UPLOAD_DIR = "uploads/customerDocuments/";
    private static final String[] ALLOWED_EXTENSIONS = {"jpg", "png", "pdf"};


    @RequestMapping("/add")
	public String addCustomerView(Model model) {
		
		CustomerForm customerForm = new CustomerForm();
		System.out.println("inside add customer view");
		List<CustomerDetails> detailsList = new ArrayList<>();
	    detailsList.add(new CustomerDetails()); // Add as many as you need
	    customerForm.setCustomerDetails(detailsList);
		model.addAttribute("customerForm", customerForm);
	    model.addAttribute("documentTypes", DocumentType.values()); // send enum list
	    return "user/add_customer";
	}

    @RequestMapping(value = "/add", method = RequestMethod.POST)
    public String saveContact(@Valid @ModelAttribute CustomerForm customerForm,
                              BindingResult result,
                              Authentication authentication,
                              HttpSession session,
                              Model model,
                              HttpServletRequest request) {

        // 1. Validate form
        if (result.hasErrors()) {
            result.getAllErrors().forEach(error -> logger.info(error.toString()));
            model.addAttribute("documentTypes", DocumentType.values());
            session.setAttribute("message",
                    Message.builder()
                            .content("Please correct the following errors")
                            .type(MessageType.red)
                            .build());
            return "user/add_customer";
        }

        try {
            // 2. Get logged-in user
            String username = Helper.getEmailOfLoggedInUser(authentication);
            User user = userService.getUserByEmail(username);

            // 3. Create Customer entity
            Customer customerEntity = new Customer();
            customerEntity.setPhoneNumber(customerForm.getPhoneNumber());
            customerEntity.setRoomNumber(customerForm.getRoomNumber());
            customerEntity.setCheckIn(customerForm.getCheckIn());
            customerEntity.setCheckOut(customerForm.getCheckOut());
            customerEntity.setAmount(customerForm.getAmount());
            customerEntity.setPaymentMode(customerForm.getPaymentMode());
            customerEntity.setDescription(customerForm.getDescription());
            customerEntity.setCreatedBy(user.getUserId());

            // 4. Save customer first (to generate ID)
            Customer savedCustomer = customerService.save(customerEntity);

            // 5. Process dynamic file uploads
            if (request instanceof MultipartHttpServletRequest multipartRequest) {
                Map<String, MultipartFile> fileMap = multipartRequest.getFileMap();

                Pattern pattern = Pattern.compile("customerDetails\\[(\\d+)]\\.(documentFront|documentBack)");

                for (Map.Entry<String, MultipartFile> entry : fileMap.entrySet()) {
                    String fieldName = entry.getKey();
                    MultipartFile file = entry.getValue();

                    if (file.isEmpty()) continue;

                    Matcher matcher = pattern.matcher(fieldName);
                    if (matcher.matches()) {
                        int index = Integer.parseInt(matcher.group(1));
                        String fieldType = matcher.group(2);

                        CustomerDetails formDetail = customerForm.getCustomerDetails().get(index);
                        formDetail.setCustomer(savedCustomer);

                        
                        String savedFileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
                    
//                        Path filePath = uploadDir.resolve(savedFileName); // try this remove below 3 line
//                        file.transferTo(filePath);
                        
                        Path filePath = Paths.get(UPLOAD_DIR, savedFileName);
                        Files.createDirectories(filePath.getParent());  // Ensure directory exists
                        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

                        if ("documentFront".equals(fieldType)) {
                            formDetail.setDocumentFrontPath(savedFileName);
                        } else if ("documentBack".equals(fieldType)) {
                            formDetail.setDocumentBackPath(savedFileName);
                        }
                    }
                }
            }
           
            for (CustomerDetails formDetail : customerForm.getCustomerDetails()) {
                formDetail.setCustomer(savedCustomer);
            }

            savedCustomer.setCustomerDetails(customerForm.getCustomerDetails());
            customerService.save(savedCustomer); // cascade save with file paths

            // 7. Success message
            session.setAttribute("message",
                    Message.builder()
                            .content("You have successfully added a new contact")
                            .type(MessageType.green)
                            .build());

            return "redirect:/user/customer/add";

        } catch (IOException e) {
            logger.error("File upload failed", e);
            session.setAttribute("message",
                    Message.builder()
                            .content("Failed to upload documents")
                            .type(MessageType.red)
                            .build());
            return "user/add_customer";
        } catch (Exception ex) {
            logger.error("Failed to save customer", ex);
            session.setAttribute("message",
                    Message.builder()
                            .content("An unexpected error occurred")
                            .type(MessageType.red)
                            .build());
            return "user/add_customer";
        }
    }


	// view contacts

	@RequestMapping
	public String viewContacts(@RequestParam(value = "page", defaultValue = "0") int page,
			@RequestParam(value = "size", defaultValue = AppConstants.PAGE_SIZE + "") int size,
			@RequestParam(value = "sortBy", defaultValue = "createdAt") String sortBy,
			@RequestParam(value = "direction", defaultValue = "desc") String direction, Model model,
			Authentication authentication) {

		// load all the user contacts
		String username = Helper.getEmailOfLoggedInUser(authentication);

//		User user = userService.getUserByEmail(username);

		Page<Customer> pageCustomer =
				customerService.getAllCustomersWithDetails(page, size, sortBy, direction);
		
		// List<CustomerDetails> details = customerService.getCustomerDetailsByCustomerId(id);

		model.addAttribute("pageCustomer", pageCustomer);
		model.addAttribute("pageSize", AppConstants.PAGE_SIZE);

		model.addAttribute("customerSearchForm", new CustomerSearchForm());

		return "user/customer";
	}

	// search handler

	@RequestMapping("/search")
	public String searchHandler(

			@ModelAttribute CustomerSearchForm customerSearchForm,
			@RequestParam(value = "size", defaultValue = AppConstants.PAGE_SIZE + "") int size,
			@RequestParam(value = "page", defaultValue = "0") int page,
			@RequestParam(value = "sortBy", defaultValue = "createdAt") String sortBy,
			@RequestParam(value = "direction", defaultValue = "desc") String direction, Model model,
			Authentication authentication) {

	//	logger.info("field {} keyword {}", customerSearchForm.getKeyword(), customerSearchForm.getValue());
//		System.out.println("field -> " + customerSearchForm.getKeyword() + " keyword " + customerSearchForm.getValue() + "size "+ size);
		var user = userService.getUserByEmail(Helper.getEmailOfLoggedInUser(authentication));

//		Page<Customer> pageCustomer = null;
//		if (customerSearchForm.getField().equalsIgnoreCase("name")) {
//			pageCustomer = customerService.searchByName(customerSearchForm.getValue(), size, page, sortBy, direction,
//					user);
//		} else if (customerSearchForm.getField().equalsIgnoreCase("email")) {
//			pageCustomer = customerService.searchByEmail(customerSearchForm.getValue(), size, page, sortBy, direction,
//					user);
//		} else if (customerSearchForm.getField().equalsIgnoreCase("phone")) {
//			pageCustomer = customerService.searchByPhoneNumber(customerSearchForm.getValue(), size, page, sortBy,
//					direction, user);
//		}

//		Page<Customer> pageCustomer = customerService.searchCustomer(customerSearchForm.getKeyword().trim(), page, size, sortBy, direction);
		Page<Customer> pageCustomer = customerService.searchCustomer(customerSearchForm.getKeyword().trim(), customerSearchForm.getPaymentMode(), page, size, sortBy, direction);
		// loop it
//		for (Customer c : pageCustomer.getContent()) {
//			System.out.println(c.getAmount());
//			
//		}
//		System.out.println("pageCustomer "+ pageCustomer.getContent());
//		logger.info("pageCustomer {}", pageCustomer);

		model.addAttribute("customerSearchForm", customerSearchForm);

		model.addAttribute("pageCustomer", pageCustomer);

		model.addAttribute("pageSize", AppConstants.PAGE_SIZE);

		return "user/customer";
	}

	// detete contact
	@RequestMapping("/delete/{contactId}")
	public String deleteContact(@PathVariable("contactId") int contactId, HttpSession session) {
		customerService.delete(contactId);
		logger.info("contactId {} deleted", contactId);

		session.setAttribute("message",
				Message.builder().content("Contact is Deleted successfully !! ").type(MessageType.green).build()

		);

		return "redirect:/user/contacts";
	}


	@GetMapping("/edit/{customerId}")
	public String showEditCustomer(@PathVariable("customerId") int customerId, Model model) {
		    Customer existing = customerService.findById(customerId);
	//	        .orElseThrow(() -> new IllegalArgumentException("Invalid Customer ID: " + customerId));
	
		    CustomerForm form = new CustomerForm();
		    form.setId(existing.getId());
		    form.setPhoneNumber(existing.getPhoneNumber());
		    form.setRoomNumber(existing.getRoomNumber());
		    form.setCheckIn(existing.getCheckIn());
		    form.setCheckOut(existing.getCheckOut());
		    form.setAmount(existing.getAmount());
		    form.setPaymentMode(existing.getPaymentMode());
		    form.setDescription(existing.getDescription());
	
		    // Set existing details directly from entity
		    form.setCustomerDetails(existing.getCustomerDetails());
	
		    model.addAttribute("customerForm", form);
		    model.addAttribute("documentTypes", DocumentType.values());
		    return "user/edit_customer";
	}

	
	@RequestMapping(value = "/edit/{id}", method = RequestMethod.POST)
	public String updateCustomer(@PathVariable int id,
	                             @Valid @ModelAttribute CustomerForm customerForm,
	                             BindingResult result,
	                             Authentication authentication,
	                             HttpSession session,
	                             Model model,
	                             HttpServletRequest request) {

	    if (result.hasErrors()) {
	        result.getAllErrors().forEach(error -> logger.info(error.toString()));
	        model.addAttribute("documentTypes", DocumentType.values());
	        session.setAttribute("message",
	                Message.builder()
	                        .content("Please correct the following errors")
	                        .type(MessageType.red)
	                        .build());
	        return "user/edit_customer";
	    }

	    try {
	        // 1. Get existing Customer from DB
	        Customer existingCustomer = customerService.findById(id);
	        if (existingCustomer == null) {
	            session.setAttribute("message",
	                    Message.builder()
	                            .content("Customer not found")
	                            .type(MessageType.red)
	                            .build());
	            return "redirect:/user/customer";
	        }

	        // 2. Get logged-in user (optional for audit)
	        String username = Helper.getEmailOfLoggedInUser(authentication);
	        User user = userService.getUserByEmail(username);

	        // 3. Update fields on existing customer
	        existingCustomer.setPhoneNumber(customerForm.getPhoneNumber());
	        existingCustomer.setRoomNumber(customerForm.getRoomNumber());
	        existingCustomer.setCheckIn(customerForm.getCheckIn());
	        existingCustomer.setCheckOut(customerForm.getCheckOut());
	        existingCustomer.setAmount(customerForm.getAmount());
	        existingCustomer.setPaymentMode(customerForm.getPaymentMode());
	        existingCustomer.setDescription(customerForm.getDescription());

	        // 4. Handle dynamic file uploads
	        if (request instanceof MultipartHttpServletRequest multipartRequest) {
	            Map<String, MultipartFile> fileMap = multipartRequest.getFileMap();

	            Pattern pattern = Pattern.compile("customerDetails\\[(\\d+)]\\.(documentFront|documentBack)");

	            for (Map.Entry<String, MultipartFile> entry : fileMap.entrySet()) {
	                String fieldName = entry.getKey();
	                MultipartFile file = entry.getValue();

	                if (file.isEmpty()) continue;

	                Matcher matcher = pattern.matcher(fieldName);
	                if (matcher.matches()) {
	                	int index = Integer.parseInt(matcher.group(1));
	                    String fieldType = matcher.group(2);

	                    // Make sure customerDetails list is not null and has enough size
	                    List<CustomerDetails> detailsList = customerForm.getCustomerDetails();
	                    if (detailsList == null || index >= detailsList.size()) {
	                        continue;
	                    }
	                    CustomerDetails formDetail = detailsList.get(index);

	                    // Link to existingCustomer for persistence
	                    formDetail.setCustomer(existingCustomer);

	                    // Save the uploaded file
	                    if (!file.isEmpty()) {
	                        // Save the uploaded file only if not empty
	                        String savedFileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
	                        Path filePath = Paths.get(UPLOAD_DIR, savedFileName);
	                        Files.createDirectories(filePath.getParent());
	                        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

	                        if ("documentFront".equals(fieldType)) {
	                            formDetail.setDocumentFrontPath(savedFileName);
	                        } else if ("documentBack".equals(fieldType)) {
	                            formDetail.setDocumentBackPath(savedFileName);
	                        }
	                    } else {
	                        // No new file uploaded, keep existing paths from database (important)
	                        // To do that, retrieve the existing detail entity and set the old path
	                        CustomerDetails existingDetail = existingCustomer.getCustomerDetails().get(index);
	                        if ("documentFront".equals(fieldType)) {
	                            formDetail.setDocumentFrontPath(existingDetail.getDocumentFrontPath());
	                        } else if ("documentBack".equals(fieldType)) {
	                            formDetail.setDocumentBackPath(existingDetail.getDocumentBackPath());
	                        }
	                    }
	                }
	            }
	        }

	        // 5. Update customer details list
	        for (CustomerDetails formDetail : customerForm.getCustomerDetails()) {
	            formDetail.setCustomer(existingCustomer);
	        }
	        existingCustomer.setCustomerDetails(customerForm.getCustomerDetails());

	        // 6. Save updated customer entity (cascade saves details)
	        customerService.save(existingCustomer);

	        // 7. Success message and redirect
	        session.setAttribute("message",
	                Message.builder()
	                        .content("Customer updated successfully")
	                        .type(MessageType.green)
	                        .build());

	        return "redirect:/user/customer";

	    } catch (IOException e) {
	        logger.error("File upload failed", e);
	        session.setAttribute("message",
	                Message.builder()
	                        .content("Failed to upload documents")
	                        .type(MessageType.red)
	                        .build());
	        return "user/edit_customer";
	    } catch (Exception ex) {
	        logger.error("Failed to update customer", ex);
	        session.setAttribute("message",
	                Message.builder()
	                        .content("An unexpected error occurred")
	                        .type(MessageType.red)
	                        .build());
	        return "user/edit_customer";
	    }
	}


}
