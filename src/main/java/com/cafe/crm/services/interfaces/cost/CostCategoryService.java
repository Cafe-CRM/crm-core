package com.cafe.crm.services.interfaces.cost;

import com.cafe.crm.models.cost.CostCategory;

import java.util.List;

public interface CostCategoryService {

	CostCategory save(CostCategory costCategory);

	void update(CostCategory costCategory);

	List<CostCategory> findAll();

	CostCategory find(Long id);

	CostCategory find(String name);

	List<CostCategory> findByNameStartingWith(String startName);

	CostCategory getSalaryCategory();

	boolean isAnyCostCategoryExist();

	boolean isSalaryCostExist();

	void delete(Long id);
}
