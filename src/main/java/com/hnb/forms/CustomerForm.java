package com.hnb.forms;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.multipart.MultipartFile;

import com.hnb.entities.CustomerDetails;
import com.hnb.enums.PaymentMode;
import com.hnb.enums.RoomNumber;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class CustomerForm {

	private int id;
	
    @NotBlank(message = "Phone Number is required")
    @Pattern(regexp = "^[0-9]{10}$", message = "Invalid Phone Number")
    private String phoneNumber;
    
 
    @NotNull(message = "Please select room number")
    private RoomNumber roomNumber;
   
    @NotNull(message = "Amount is required")
	private Integer amount;
    @NotNull(message = "Payment mode is required")
	private PaymentMode paymentMode;

    @NotNull(message = "Check-in date is required")
    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    private LocalDateTime  checkIn;
    @NotNull(message = "Check-out date is required")
    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    private LocalDateTime checkOut;
   
    private String description;

    @Valid
    private List<CustomerDetails> customerDetails = new ArrayList<>();


    private MultipartFile documentFront;

    private MultipartFile documentBack;


}
