package com.yuanzhi.finledger.repository;

import com.yuanzhi.finledger.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AccountRepository extends JpaRepository<Account, Integer> {
    Optional<Account> findByCode(String code);
    boolean existsByCode(String code);
    List<Account> findByCategory(String category);
    List<Account> findByParentID(Integer parentID);
    List<Account> findByParentIDIsNull(); // 根科目
}

