package com.hnb.forms;

import java.time.LocalDate;

import org.springframework.format.annotation.DateTimeFormat;

import com.hnb.enums.Owners;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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
public class WithdrawForm {

	@NotNull(message = "Please select an owner")
	@Enumerated(EnumType.STRING)
	private Owners owners;
	@Min(value = 0, message = "Cash amount must be at least 1 Rupee")
	private Double cash;
	@Min(value = 0, message = "Online amount must be at least 1 Rupee")
	private Double online;
	
    @NotNull(message = "Expense date is required")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
	private LocalDate withdrawDate;
	private String notes;


}
