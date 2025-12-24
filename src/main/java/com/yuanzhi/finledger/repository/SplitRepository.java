package com.yuanzhi.finledger.repository;

import com.yuanzhi.finledger.entity.Split;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SplitRepository extends JpaRepository<Split, Integer> {
    List<Split> findByEntryID(Integer entryID);
    List<Split> findByAccountID(Integer accountID);
}


