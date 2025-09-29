package com.hnb.repsitories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.hnb.entities.Withdraw;

@Repository
public interface WithdrawRepo extends JpaRepository<Withdraw, Integer> {



	
}
