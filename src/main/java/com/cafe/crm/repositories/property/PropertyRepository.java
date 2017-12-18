package com.cafe.crm.repositories.property;


import com.cafe.crm.models.property.Property;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PropertyRepository extends JpaRepository<Property, Long> {

	Property findByNameAndCompanyId(String name, Long companyId);

	List<Property> findByCompanyId(Long companyId);

	List<Property> findByNameIn(String... name);
}
