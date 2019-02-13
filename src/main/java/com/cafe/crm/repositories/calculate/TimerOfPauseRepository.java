package com.cafe.crm.repositories.calculate;


import com.cafe.crm.models.client.TimerOfPause;
import com.cafe.crm.repositories.customRepository.CommonRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;

public interface TimerOfPauseRepository extends JpaRepository<TimerOfPause, Long> {

	TimerOfPause findTimerOfPauseByIdOfClient(Long id);

	@Query("SELECT t FROM TimerOfPause t WHERE t.company.id = :companyId AND t.startTime BETWEEN :startDate and :endDate " +
			"OR t.endTime BETWEEN :startDate and :endDate")
	Set<TimerOfPause> findByDatesAndCompanyId(@Param("startDate") LocalDateTime startDate,
											  @Param("endDate") LocalDateTime endDate,
											  @Param("companyId") Long companyId);
}