package com.hnb.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.hnb.enums.DocumentType;
import com.hnb.enums.Gender;
import com.hnb.validators.ValidAgeByGender;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@ValidAgeByGender(message = "Please enter a valid age for the selected gender")
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CustomerDetails {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;

	@ManyToOne
	@JoinColumn(name = "customer_id")
	@JsonBackReference
	private Customer customer;
	@NotBlank(message = "Name is required")
	private String name;

	@NotNull(message = "Gender is required")
	@Enumerated(EnumType.STRING)
	private Gender gender;
	private Integer age;
	
	@NotBlank(message = "Pin code is required")
	@Pattern(regexp = "\\d{6}", message = "Pin code must be exactly 6 digits")
	private String pinCode;
	@NotBlank(message = "City is required")
	private String city;
	@NotBlank(message = "Address is required")
	private String address;

	@Enumerated(EnumType.STRING)
	private DocumentType documentType;
	
    private String documentFrontPath;  // store the file path or name

    private String documentBackPath;

}
