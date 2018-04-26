package com.cafe.crm.services.interfaces.menu;


import com.cafe.crm.models.menu.Category;

import java.util.List;

public interface CategoriesService {

	List<Category> findAll();

	Category getOne(Long id);

	Category saveAndFlush(Category category);

	void delete(Long id);

	List<Category> sortProductListAndGetAllCategories();

}
