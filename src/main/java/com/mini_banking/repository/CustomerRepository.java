package com.mini_banking.repository;


import org.springframework.data.jpa.repository.JpaRepository;

import com.mini_banking.entity.Customer;

import java.util.Optional;

public interface CustomerRepository extends JpaRepository<Customer, Long> {

    Optional<Customer> findByEmail(String email);
}