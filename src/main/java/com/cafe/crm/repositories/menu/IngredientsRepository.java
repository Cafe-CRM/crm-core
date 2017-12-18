package com.cafe.crm.repositories.menu;


import com.cafe.crm.models.menu.Ingredients;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface IngredientsRepository extends JpaRepository<Ingredients, Long> {

	Ingredients findByNameAndCompanyId(String name, Long companyId);
	List<Ingredients> findByCompanyId(Long companyId);
}
