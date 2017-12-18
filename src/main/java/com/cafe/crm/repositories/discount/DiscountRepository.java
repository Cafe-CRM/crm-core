package com.cafe.crm.repositories.discount;

import com.cafe.crm.models.discount.Discount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface DiscountRepository extends JpaRepository<Discount, Long> {

	@Query("SELECT c FROM Discount c WHERE c.isOpen = true AND c.company.id = :companyId")
	List<Discount> getAllOpenAndCompanyId(@Param("companyId") Long companyId);

	List<Discount> findByCompanyId(Long companyId);
}