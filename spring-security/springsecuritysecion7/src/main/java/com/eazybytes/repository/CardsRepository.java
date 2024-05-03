package com.eazybytes.repository;

import com.eazybytes.model.Cards;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CardsRepository extends JpaRepository<Cards, Long> {

    List<Cards> findByCustomerId(Long customerId);
}
