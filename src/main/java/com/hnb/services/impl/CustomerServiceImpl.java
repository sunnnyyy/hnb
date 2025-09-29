package com.hnb.services.impl;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.hnb.entities.Customer;
import com.hnb.enums.PaymentMode;
import com.hnb.repsitories.CustomerRepo;
import com.hnb.repsitories.UserRepo;
import com.hnb.services.CustomerService;

@Service
public class CustomerServiceImpl implements CustomerService
{

    private final UserRepo userRepo;
    @Autowired
    private CustomerRepo customerRepo;


    CustomerServiceImpl(UserRepo userRepo) {
        this.userRepo = userRepo;
    }


    @Override
    public Customer save(Customer contact) {
      
        return customerRepo.save(contact);
    }

//    @Override
//    public Customer update(Customer contact) {
//        var customerOld = customerRepo.findById(contact.getId())
//                .orElseThrow(() -> new ResourceNotFoundException("Contact not found"));
////        customerOld.setName(contact.getName());
////        customerOld.setEmail(contact.getEmail());
////        customerOld.setPhoneNumber(contact.getPhoneNumber());
////        customerOld.setAddress(contact.getAddress());
////        customerOld.setDescription(contact.getDescription());
//      
////        contactOld.setCloudinaryImagePublicId(contact.getCloudinaryImagePublicId());
//
//        return customerRepo.save(customerOld);
//    }

    @Override
    public List<Customer> getAll() {
        return customerRepo.findAll();
    }

    @Override
    public Customer findById(int id) {
    		return customerRepo.findById(id);
    }

    @Override
    public Customer getById(int id) {
        return customerRepo.findById(id);
//                .orElseThrow(() -> new ResourceNotFoundException("Contact not found with given id " + id));
    }

    @Override
    public void delete(int id) {
        var contact = customerRepo.findById(id);
//                .orElseThrow(() -> new ResourceNotFoundException("Contact not found with given id " + id));
        customerRepo.delete(contact);

    }

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
    
    @Override
    public Page<Customer> getAll(int page, int size, String sortBy, String direction) {
    	
    	Sort sort = direction.equals("desc") ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
    	
    	var pageable = PageRequest.of(page, size, sort);
    	
    return customerRepo.findAll(pageable);  
    	
    }

//    @Override
//    public Page<Customer> searchByName(String nameKeyword, int size, int page, String sortBy, String order, User user) {
//
//        Sort sort = order.equals("desc") ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
//        var pageable = PageRequest.of(page, size, sort);
//        return customerRepo.findByUserAndNameContaining(user, nameKeyword, pageable);
//    }

//    @Override
//    public Page<Customer> searchByEmail(String emailKeyword, int size, int page, String sortBy, String order,
//            User user) {
//        Sort sort = order.equals("desc") ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
//        var pageable = PageRequest.of(page, size, sort);
//        return customerRepo.findByUserAndEmailContaining(user, emailKeyword, pageable);
//    }

//    @Override
//    public Page<Customer> searchByPhoneNumber(String phoneNumberKeyword, int size, int page, String sortBy,
//            String order, User user) {
//
//        Sort sort = order.equals("desc") ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
//        var pageable = PageRequest.of(page, size, sort);
//        return customerRepo.findByUserAndPhoneNumberContaining(user, phoneNumberKeyword, pageable);
//    }

    
    
//    public List<Customer> getAllCustomersWithDetails() {
//        return customerRepo.findAllWithCustomerDetails();
//    }
    
    @Override
    public Page<Customer> getAllCustomersWithDetails(int page, int size, String sortBy, String direction) {
    	
    	Sort sort = direction.equals("desc") ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
    	
    	var pageable = PageRequest.of(page, size, sort);
    	
    return customerRepo.findAllWithCustomerDetails(pageable);  
    	
    }

    public Page<Customer> searchCustomer(String keyword, PaymentMode paymentMode, int page, int size, String sortBy, String direction) {
	  	Sort sort = direction.equals("desc") ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
	    	var pageable = PageRequest.of(page, size, sort);
	    return customerRepo.searchCustomer(keyword, paymentMode, pageable);
    }
 
    public long countCustomersInCurrentMonth() {
        LocalDate now = LocalDate.now();
        int month = now.getMonthValue();  // 1-12 for Jan-Dec
        int year = now.getYear();

        return customerRepo.countCustomersByCheckInMonthAndYear(month, year);
    }

}
