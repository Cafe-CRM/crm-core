package com.cafe.crm.services.interfaces.cost;


import com.cafe.crm.models.cost.Cost;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

public interface CostService {

	void save(Cost cost);

	void update(Cost cost);

	void delete(Long id);

	void offVisibleStatus(Long id);

	void offVisibleStatus(long[] ids);

	List<Cost> findByCategoryNameAndDateBetween(String categoryName, LocalDate from, LocalDate to);

	List<Cost> findByNameAndDateBetween(String name, LocalDate from, LocalDate to);

	List<Cost> findByNameAndCategoryNameAndDateBetween(String name, String categoryName, LocalDate from, LocalDate to);

	List<Cost> findByDateBetween(LocalDate from, LocalDate to);

	Set<Cost> findByNameStartingWith(String startName);

	List<Cost> findByDateAndVisibleTrue(LocalDate date);

	List<Cost> findByDateAndCategoryNameAndVisibleTrue(LocalDate date, String name);

	List<Cost> findByShiftIdAndCategoryNameNot(Long shiftId, String name);

	List<Cost> findByShiftId(Long shiftId);

	List<Cost> findByCategoryName(String name);
}
