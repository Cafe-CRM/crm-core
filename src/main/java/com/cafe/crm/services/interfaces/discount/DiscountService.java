package com.cafe.crm.services.interfaces.discount;


import com.cafe.crm.models.discount.Discount;

import java.util.List;

public interface DiscountService {

	Discount save(Discount discount);

	void delete(Discount discount);

	List<Discount> getAll();

	Discount getOne(Long id);

	void deleteById(Long id);

	List<Discount> getAllOpen();
}

