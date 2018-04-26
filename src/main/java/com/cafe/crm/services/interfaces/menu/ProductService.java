package com.cafe.crm.services.interfaces.menu;

import com.cafe.crm.dto.WrapperOfProduct;
import com.cafe.crm.models.menu.Ingredients;
import com.cafe.crm.models.menu.Product;
import com.cafe.crm.models.user.Position;

import java.util.List;
import java.util.Map;


public interface ProductService {

	List<Product> findAll();

	List<Product> saveAll(List<Product> products);

	Product saveAndFlush(Product product);

	Product findOne(Long id);

	void delete(Long id);

	void deleteIngredientFromProducts(List<Product> products, Ingredients ingredients);

	Product findByNameAndDescriptionAndCost(String name, String description, Double cost);

	List<Product> findAllOrderByRatingDesc();

	void reduceIngredientAmount(Product product);

	Map<Position,Integer> createStaffPercent(WrapperOfProduct wrapper);

	List<Product> findByIds(long[] ids);

	List<Product> findAllReceiptProducts(Ingredients ingredient);
}
