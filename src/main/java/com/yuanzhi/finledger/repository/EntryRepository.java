package com.yuanzhi.finledger.repository;

import com.yuanzhi.finledger.entity.Entry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EntryRepository extends JpaRepository<Entry, Integer> {
    List<Entry> findByTransactionID(Integer transactionID);
    List<Entry> findByUserID(Integer userID);
    List<Entry> findByStatus(String status);
    
    @Query("SELECT e FROM Entry e WHERE e.status = 'SUBMITTED' ORDER BY e.createdDate ASC")
    List<Entry> findSubmittedEntries();
}


