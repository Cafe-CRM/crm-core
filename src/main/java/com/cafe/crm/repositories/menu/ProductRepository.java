package com.cafe.crm.repositories.menu;

import com.cafe.crm.models.menu.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;


public interface ProductRepository extends JpaRepository<Product, Long> {

	Product findByNameAndDescriptionAndCostAndCompanyId(String name, String description, Double cost, Long companyId);

	List<Product> findByCompanyIdOrderByRatingDescNameAsc(Long companyId);

	List<Product> findByCompanyId(Long companyId);


}
