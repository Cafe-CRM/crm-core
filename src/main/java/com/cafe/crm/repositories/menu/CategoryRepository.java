package com.cafe.crm.repositories.menu;

import com.cafe.crm.models.menu.Category;
import com.cafe.crm.repositories.customRepository.CommonRepository;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;


public interface CategoryRepository extends JpaRepository<Category, Long> {
	List<Category> findByCompanyId(Long companyId);
}
