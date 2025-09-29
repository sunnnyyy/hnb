package com.hnb.forms;

import com.hnb.enums.PaymentMode;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CustomerSearchForm {

    private String keyword;
    private PaymentMode paymentMode;  // <-- add this field with getter/setter

}
