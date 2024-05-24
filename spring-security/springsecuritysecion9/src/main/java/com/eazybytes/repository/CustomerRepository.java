package com.eazybytes.repository;

import com.eazybytes.model.Customer;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CustomerRepository extends JpaRepository<Customer, Long> {

    List<Customer> findByEmail(String email);
}
