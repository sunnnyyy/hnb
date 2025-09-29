package com.hnb.forms;

import java.time.LocalDate;

import com.hnb.enums.Owners;

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
public class WithdrawFormDTO {

	private Owners ownerName;
	private double cashAmount;
	private double onlineAmount;
	private LocalDate withdrawDate;
	private String comment;
	//private String formToken;
}
