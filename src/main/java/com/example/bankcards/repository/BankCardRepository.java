package com.example.bankcards.repository;

import com.example.bankcards.entity.BankCard;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Pageable;

import java.util.List;

@Repository
public interface BankCardRepository extends JpaRepository<BankCard, Long> {

    List<BankCard> findByUserUsername(String username);

    List<BankCard> findByUserUsernameAndActive(String username, boolean active);

    Page<BankCard> findByUserUsernameAndActive(String username, boolean active, Pageable pageable);

    List<BankCard> findTop5ByUserUsernameOrderByIdDesc(String username);

}
