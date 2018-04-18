package com.cafe.crm.repositories.cost;

import com.cafe.crm.models.cost.Cost;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

public interface CostRepository extends JpaRepository<Cost, Long> {

	List<Cost> findByCategoryNameIgnoreCaseAndVisibleIsTrueAndDateBetweenAndCompanyIdAndCategoryIsSalaryCostFalse(String name, LocalDate from, LocalDate to, Long companyId);

	List<Cost> findByCategoryNameIgnoreCaseAndVisibleIsTrueAndDateBetweenAndCompanyId(String name, LocalDate from, LocalDate to, Long companyId);

	List<Cost> findByCategoryNameAndVisibleIsTrueAndCompanyIdAndCategoryIsSalaryCostFalse(String name, Long companyId);

	List<Cost> findByNameIgnoreCaseAndVisibleIsTrueAndDateBetweenAndCompanyIdAndCategoryIsSalaryCostFalse(String name, LocalDate from, LocalDate to, Long companyId);

	List<Cost> findByNameIgnoreCaseAndVisibleIsTrueAndDateBetweenAndCompanyId(String name, LocalDate from, LocalDate to, Long companyId);

	List<Cost> findByNameIgnoreCaseAndCategoryNameIgnoreCaseAndVisibleIsTrueAndDateBetweenAndCompanyIdAndCategoryIsSalaryCostFalse(String name, String categoryName, LocalDate from, LocalDate to, Long companyId);

	List<Cost> findByNameIgnoreCaseAndCategoryNameIgnoreCaseAndVisibleIsTrueAndDateBetweenAndCompanyId(String name, String categoryName, LocalDate from, LocalDate to, Long companyId);

	List<Cost> findByVisibleIsTrueAndDateBetweenAndCompanyIdAndCategoryIsSalaryCostFalse(LocalDate from, LocalDate to, Long companyId);

	List<Cost> findByVisibleIsTrueAndDateBetweenAndCompanyId(LocalDate from, LocalDate to, Long companyId);

	Set<Cost> findByNameStartingWithAndCompanyIdAndCategoryIsSalaryCostFalse(String startName, Long companyId);

	List<Cost> findByIdIn(long[] ids);

	List<Cost> findByDateAndVisibleTrueAndCompanyIdAndCategoryIsSalaryCostFalse(LocalDate date, Long companyId);

	List<Cost> findByDateAndCategoryNameAndVisibleTrueAndCompanyIdAndCategoryIsSalaryCostFalse(LocalDate date, String name, Long companyId);

	List<Cost> findByShiftIdAndCategoryNameNotAndVisibleIsTrueAndCategoryIsSalaryCostFalse(Long shiftId, String name);

	List<Cost> findByShiftIdAndVisibleIsTrueAndCategoryIsSalaryCostFalse(Long shiftId);

	List<Cost> findByDateBetweenAndCategoryIsSalaryCostTrue(LocalDate from, LocalDate to);

	List<Cost> findByShiftIdAndCategoryIsSalaryCostTrue(Long shiftId);

}
