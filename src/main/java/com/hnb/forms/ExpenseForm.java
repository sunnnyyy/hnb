package com.hnb.forms;

import java.time.LocalDateTime;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.multipart.MultipartFile;

import com.hnb.enums.ExpenseType;
import com.hnb.enums.PaymentMode;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
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
public class ExpenseForm {

	@NotNull(message = "Expense Type is required")
	private ExpenseType expenseType;
	@NotNull(message = "Amount is required")
	@Min(value = 1, message = "Amount must be at least 1 Rupee")
	private double amount;
    @NotNull(message = "Payment mode is required")
	private PaymentMode paymentMode;
    @NotNull(message = "Expense date is required")
    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    private LocalDateTime expenseDate;
	private String notes;
	private MultipartFile billUrl;
	

}
