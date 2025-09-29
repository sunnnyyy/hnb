package com.hnb.entities;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

import org.hibernate.annotations.CreationTimestamp;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.hnb.enums.PaymentMode;
import com.hnb.enums.RoomNumber;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Data
public class Customer {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;
	private String phoneNumber;
	@Enumerated(EnumType.STRING)
	private RoomNumber roomNumber;
	private LocalDateTime checkIn;
	private LocalDateTime checkOut;
	private int amount;
	@Enumerated(EnumType.STRING)
	private PaymentMode paymentMode;
	private boolean isPaid = true;
	private boolean isApproved = false;
	@CreationTimestamp
	@Temporal(TemporalType.TIMESTAMP)
	private Date createdAt;
	private String createdBy;
	@Column(length = 1000)
	private String description;

	@OneToMany(mappedBy = "customer", cascade = CascadeType.ALL, orphanRemoval = true)
	@JsonManagedReference
	private List<CustomerDetails> customerDetails;

//	public List<CustomerDetails> getCustomerDetails() {
//		return customerDetails;
//	}
//
//	public void setCustomerDetails(List<CustomerDetails> customerDetails) {
//		this.customerDetails = customerDetails;
//	}

	// private List<String> socialLinks=new ArrayList<>();
//    private String cloudinaryImagePublicId;

	// Store JSON in MySQL column
//    @Type(JsonType.class)
//    @Column(columnDefinition = "json")
//    private List<Map<String, String>> documents;

}
