package com.cafe.crm.repositories.shift;

import com.cafe.crm.models.shift.Shift;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;


public interface ShiftRepository extends JpaRepository<Shift, Long> {

	@Query("SELECT u FROM Shift u WHERE u.company.id = :companyId AND u.id =(select max(id) from Shift)")
		//последняя смена в базе
	Shift getLastAndCompanyId(@Param ("companyId") Long companyId);

	@Query("SELECT e FROM Shift e WHERE e.company.id = :companyId AND e.shiftDate BETWEEN :startDate and :endDate")
	Set<Shift> findByDatesAndCompanyId(@Param("startDate") LocalDate startDate,
						   @Param("endDate") LocalDate endDate,
						   @Param("companyId") Long companyId);

	Shift findByShiftDateAndCompanyId(LocalDate date, Long companyId);

	List<Shift> findByCompanyId(Long companyId);

	Shift findById(Long id);
}
