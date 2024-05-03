package com.eazybytes.repository;

import com.eazybytes.model.Accounts;
import com.eazybytes.model.Loans;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccountsRepository extends JpaRepository<Accounts, Loans> {
    Accounts findByCustomerId(Long customerId);
}
