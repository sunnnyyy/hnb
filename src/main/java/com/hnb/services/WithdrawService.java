package com.hnb.services;

import org.springframework.data.domain.Page;

import com.hnb.entities.Withdraw;

public interface WithdrawService {

	Withdraw save(Withdraw withdraw);
    Page<Withdraw> getAll(int page, int size, String sortField, String sortDirection);

}
