package com.cafe.crm.repositories.checklist;


import com.cafe.crm.models.checklist.Checklist;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ChecklistRepository extends JpaRepository<Checklist, Long> {

	@Query("SELECT c FROM Checklist c where c.onOpenShiftText is not null AND c.company.id = :companyId")
	List<Checklist> getAllForOpenShiftAndCompanyId(@Param("companyId") Long companyId);

	@Query("SELECT c FROM Checklist c where c.onCloseShiftText is not null AND c.company.id = :companyId")
	List<Checklist> getAllForCloseShiftAndCompanyId(@Param("companyId") Long companyId);

	List<Checklist> findByCompanyId(Long companyId);
}
