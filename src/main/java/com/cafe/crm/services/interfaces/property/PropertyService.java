package com.cafe.crm.services.interfaces.property;


import com.cafe.crm.models.property.Property;

import java.util.List;

public interface PropertyService {

	void save(Property property);

	List<Property> findAll();

	Property findByName(String name);

	void delete(Long id);

	void saveCollection(List<Property> list);

	List<Property> findByNameIn(String... name);

}
