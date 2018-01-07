package com.cafe.crm.repositories.menu;

import com.cafe.crm.models.menu.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;


public interface ProductRepository extends JpaRepository<Product, Long> {

	Product findByDeletedIsFalseAndNameAndDescriptionAndCostAndCompanyId(String name, String description, Double cost, Long companyId);

	List<Product> findByDeletedIsFalseAndCompanyIdOrderByRatingDescNameAsc(Long companyId);

	List<Product> findByDeletedIsFalseAndCompanyId(Long companyId);

	List<Product> findByIdIn(long[] ids);
}
