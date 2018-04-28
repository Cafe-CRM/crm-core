package com.cafe.crm.services.interfaces.cost;


import com.cafe.crm.models.cost.Cost;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

public interface CostService {

	Cost save(Cost cost);

	Cost getOne(Long costId);

	void update(Cost cost);

	void delete(Long id);

	Cost offVisibleStatus(Long id);

	List<Cost> offVisibleStatus(long[] ids);

	List<Cost> findOtherCostByCategoryNameAndDateBetween(String categoryName, LocalDate from, LocalDate to);

	List<Cost> findAllCostByCategoryNameAndDateBetween(String categoryName, LocalDate from, LocalDate to);

	List<Cost> findOtherCostByNameAndDateBetween(String name, LocalDate from, LocalDate to);

	List<Cost> findAllCostByNameAndDateBetween(String name, LocalDate from, LocalDate to);

	List<Cost> findOtherCostByNameAndCategoryNameAndDateBetween(String name, String categoryName, LocalDate from, LocalDate to);

	List<Cost> findAllCostByNameAndCategoryNameAndDateBetween(String name, String categoryName, LocalDate from, LocalDate to);

	List<Cost> findOtherCostByDateBetween(LocalDate from, LocalDate to);

	List<Cost> findAllCostByDateBetween(LocalDate from, LocalDate to);

	Set<Cost> findOtherCostByNameStartingWith(String startName);

	List<Cost> findOtherCostByDateAndVisibleTrue(LocalDate date);

	List<Cost> findOtherCostByDateAndCategoryNameAndVisibleTrue(LocalDate date, String name);

	List<Cost> findOtherCostByShiftIdAndCategoryNameNot(Long shiftId, String name);

	List<Cost> findOtherCostByShiftId(Long shiftId);

	List<Cost> findOtherCostByCategoryName(String name);

	List<Cost> findSalaryCostAtShift(Long shiftId);

	List<Cost> findSalaryCostByDateBetween(LocalDate from, LocalDate to);
}
