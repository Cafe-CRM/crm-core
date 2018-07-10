package com.cafe.crm.services.interfaces.cost;


import com.cafe.crm.models.cost.Cost;
import com.cafe.crm.models.shift.Shift;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

public interface CostService {
	Cost save(Cost cost);

	Cost getOne(Long costId);

	void update(Cost cost);

	void delete(Long id);

	List<Cost> offVisibleStatus(long[] ids);

	List<Cost> findAllCostByCategoryNameAndDateBetween(String categoryName, LocalDate from, LocalDate to);

	List<Cost> findAllCostByNameAndDateBetween(String name, LocalDate from, LocalDate to);

	List<Cost> findAllCostByNameAndCategoryNameAndDateBetween(String name, String categoryName, LocalDate from, LocalDate to);

	List<Cost> findOtherCostByDateBetween(LocalDate from, LocalDate to);

	List<Cost> findAllCostByDateBetween(LocalDate from, LocalDate to);

	Set<Cost> findOtherCostByNameStartingWith(String startName);

	List<Cost> findOtherCostByShiftId(Shift shift);

	List<Cost> findSalaryCostAtShift(Shift shift);

	List<Cost> findOtherCostAtShift(Shift shift);

	List<Cost> findSalaryCostByDateBetween(LocalDate from, LocalDate to);

	void deleteAllCostByShift(Shift shift);

	void deleteAllCostByShiftIdIn(long[] ids);
}
