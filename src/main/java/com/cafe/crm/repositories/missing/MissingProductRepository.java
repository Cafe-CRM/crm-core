package com.cafe.crm.repositories.missing;

import com.cafe.crm.models.missingModel.MissingProduct;
import com.cafe.crm.models.shift.Shift;

import com.cafe.crm.repositories.customRepository.CommonRepository;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MissingProductRepository extends JpaRepository<MissingProduct, Long> {
    List<MissingProduct> findByShiftAndCompanyId(Shift shift, Long companyId);

    @EntityGraph(value = "MissingProduct.category", type = EntityGraph.EntityGraphType.LOAD)
    List<MissingProduct> readByShiftAndCompanyId(Shift shift, Long companyId);

    void deleteAllByShiftAndCompanyId(Shift shift, Long companyId);

    void deleteAllByShiftIdInAndCompanyId(long[] ids, Long companyId);
}
