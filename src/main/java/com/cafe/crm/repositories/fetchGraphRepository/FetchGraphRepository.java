package com.cafe.crm.repositories.fetchGraphRepository;

import com.cafe.crm.models.missingModel.MissingProduct;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


public interface FetchGraphRepository extends JpaRepository<MissingProduct, Long>, FetchGraphRepositoryCustom<Long, MissingProduct> {
}
