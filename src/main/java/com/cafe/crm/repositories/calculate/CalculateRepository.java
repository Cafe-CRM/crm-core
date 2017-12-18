package com.cafe.crm.repositories.calculate;

import com.cafe.crm.models.client.Calculate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;


public interface CalculateRepository extends JpaRepository<Calculate, Long> {

	@Query("SELECT DISTINCT c FROM Calculate c JOIN FETCH c.client cc WHERE c.state = true AND cc.state = true AND cc.deleteState = false AND cc.company.id = :companyId")
	List<Calculate> getAllOpenAndCompanyId(@Param("companyId") Long companyId);

	@Query("SELECT DISTINCT c FROM Calculate c JOIN FETCH c.client cc WHERE c.id =:id AND cc.state = true AND cc.deleteState = false AND cc.company.id = :companyId")
	Calculate getAllOpenOnCalculateAndCompanyId(@Param("id") Long calculateId,
									@Param("companyId") Long companyId);

	Calculate findByClientId(Long clientId);

	List<Calculate> findByCompanyId(Long companyId);
}
