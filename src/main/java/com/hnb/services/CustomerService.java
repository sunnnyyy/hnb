package com.hnb.services;

import java.util.List;

import org.springframework.data.domain.Page;

import com.hnb.entities.Customer;
import com.hnb.enums.PaymentMode;

public interface CustomerService {
    // save contacts
	Customer save(Customer customer);

    // update contact
	//Customer update(Customer contact);

    // get contacts
    List<Customer> getAll();

    // get contact by id

    Customer getById(int id);

    Customer findById(int id);

    // delete contact

    void delete(int id);

   Page<Customer> getAll(int page, int size, String sortField, String sortDirection);

   
   Page<Customer> getAllCustomersWithDetails(int page, int size, String sortField, String sortDirection);

   Page<Customer> searchCustomer(String keyword, PaymentMode paymentMode, int page, int size, String sortBy, String sortDirection);

   public long countCustomersInCurrentMonth();

}
