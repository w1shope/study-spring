package com.eazybytes.repository;

import com.eazybytes.model.Loans;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LoanRepository extends JpaRepository<Loans, Long> {

    List<Loans> findByCustomerIdOrderByStartDtDesc(Long customerId);
}
