package com.nst.scheduledispatcher.repository;

import com.nst.scheduledispatcher.model.DispatchRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ScheduledDispatchRepository extends JpaRepository<DispatchRecord, Long> {
}
