package com.cafe.crm.repositories.cost;

import com.cafe.crm.models.cost.CostCategory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;


public interface CostCategoryRepository extends JpaRepository<CostCategory, Long> {

	CostCategory findByNameIgnoreCaseAndCompanyId(String name, long companyId);

	List<CostCategory> findByNameStartingWithAndCompanyId(String startName, long companyId);

	List<CostCategory> findByCompanyId(Long companyId);
}
