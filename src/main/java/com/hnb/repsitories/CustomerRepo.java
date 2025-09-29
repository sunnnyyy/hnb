package com.hnb.repsitories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.hnb.entities.Customer;
import com.hnb.enums.PaymentMode;

@Repository
public interface CustomerRepo extends JpaRepository<Customer, Integer> {
    // find the contact by user
    // custom finder method
//    Page<Customer> findByUser(User user, Pageable pageable);
    Customer findById(int id);
	
    Page<Customer> findAll(Pageable pageable);

		
    @Query("SELECT c FROM Customer c LEFT JOIN FETCH c.customerDetails")
    Page<Customer> findAllWithCustomerDetails(Pageable pageable);
    
    // custom query method
//    @Query("SELECT c FROM customer c WHERE c.id = :userId")
//    List<Customer> findByUserId(@Param("userId") String userId);

//    Page<Customer> findByUserAndNameContaining(User user, String namekeyword, Pageable pageable);

//    Page<Customer> findByUserAndEmailContaining(User user, String emailkeyword, Pageable pageable);

//    Page<Customer> findByUserAndPhoneNumberContaining(User user, String phonekeyword, Pageable pageable);

    @Query("SELECT c FROM Customer c JOIN c.customerDetails cd " +
    	       "WHERE (LOWER(c.phoneNumber) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
    	       "OR LOWER(c.roomNumber) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
    	       "OR STR(c.amount) LIKE CONCAT('%', :keyword, '%') " +
    	       "OR LOWER(c.paymentMode) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
    	       "OR LOWER(c.description) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
    	       "OR LOWER(cd.name) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
    	       "OR STR(cd.age) LIKE CONCAT('%', :keyword, '%') " +
    	       "OR LOWER(cd.gender) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
    	       "OR LOWER(cd.pinCode) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
    	       "OR LOWER(cd.city) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
    	       "OR LOWER(cd.address) LIKE LOWER(CONCAT('%', :keyword, '%'))) " + 
    	       "AND (:paymentMode IS NULL OR c.paymentMode = :paymentMode)")
    Page<Customer> searchCustomer(@Param("keyword") String keyword,
    								 @Param("paymentMode") PaymentMode paymentMode,
 	                             Pageable pageable);

    @Query("SELECT COUNT(c) FROM Customer c WHERE MONTH(c.checkIn) = :month AND YEAR(c.checkIn) = :year")
    long countCustomersByCheckInMonthAndYear(@Param("month") int month, @Param("year") int year);

}
