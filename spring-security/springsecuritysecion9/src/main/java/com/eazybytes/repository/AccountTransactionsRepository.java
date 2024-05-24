package com.eazybytes.repository;

import com.eazybytes.model.AccountTransactions;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccountTransactionsRepository extends JpaRepository<AccountTransactions, Long> {

    List<AccountTransactions> findByCustomerIdOrderByTransactionDtDesc(Long customerId);
}
