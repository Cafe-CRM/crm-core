package com.cafe.crm.repositories.menu;

import com.cafe.crm.models.menu.Ingredients;
import com.cafe.crm.models.menu.Product;
import com.cafe.crm.repositories.customRepository.CommonRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Map;


public interface ProductRepository extends JpaRepository<Product, Long> {

	Product findByDeletedIsFalseAndNameAndDescriptionAndCostAndCompanyId(String name, String description, Double cost, Long companyId);

	List<Product> findByDeletedIsFalseAndCompanyIdOrderByRatingDescNameAsc(Long companyId);

	List<Product> findByDeletedIsFalseAndCompanyId(Long companyId);

	List<Product> findByIdIn(long[] ids);

	@Query("SELECT p FROM Product p JOIN p.recipe r WHERE KEY(r) = :ingredient")
	List<Product> findAllReceiptProducts(@Param("ingredient") Ingredients ingredient);
}
