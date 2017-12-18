package com.cafe.crm.repositories.layerproduct;

import com.cafe.crm.models.client.LayerProduct;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LayerProductRepository extends JpaRepository<LayerProduct, Long> {
	List<LayerProduct> findByCompanyId(Long companyId);
}
