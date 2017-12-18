package com.cafe.crm.repositories.cost;

import com.cafe.crm.models.cost.Cost;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

public interface CostRepository extends JpaRepository<Cost, Long> {

	List<Cost> findByCategoryNameIgnoreCaseAndVisibleIsTrueAndDateBetweenAndCompanyId(String name, LocalDate from, LocalDate to, Long companyId);

	List<Cost> findByCategoryNameAndVisibleIsTrueAndCompanyId(String name, Long companyId);

	List<Cost> findByNameIgnoreCaseAndVisibleIsTrueAndDateBetweenAndCompanyId(String name, LocalDate from, LocalDate to, Long companyId);

	List<Cost> findByNameIgnoreCaseAndCategoryNameIgnoreCaseAndVisibleIsTrueAndDateBetweenAndCompanyId(String name, String categoryName, LocalDate from, LocalDate to, Long companyId);

	List<Cost> findByVisibleIsTrueAndDateBetweenAndCompanyId(LocalDate from, LocalDate to, Long companyId);

	Set<Cost> findByNameStartingWithAndCompanyId(String startName, Long companyId);

	List<Cost> findByIdIn(long[] ids);

	List<Cost> findByDateAndVisibleTrueAndCompanyId(LocalDate date, Long companyId);

	List<Cost> findByDateAndCategoryNameAndVisibleTrueAndCompanyId(LocalDate date, String name, Long companyId);

	List<Cost> findByShiftIdAndCategoryNameNotAndVisibleIsTrue(Long shiftId, String name);

	List<Cost> findByShiftIdAndVisibleIsTrue(Long shiftId);



}
