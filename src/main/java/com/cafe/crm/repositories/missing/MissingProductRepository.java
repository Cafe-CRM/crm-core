package com.cafe.crm.repositories.missing;

import com.cafe.crm.models.missingModel.MissingProduct;
import com.cafe.crm.models.shift.Shift;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MissingProductRepository extends JpaRepository<MissingProduct, Long> {

    List<MissingProduct> findByShiftAndCompanyId(Shift shift, Long companyId);

    @EntityGraph(value = "MissingProduct.category", type = EntityGraph.EntityGraphType.LOAD)
    List<MissingProduct> readByShiftAndCompanyId(Shift shift, Long companyId);
}
