package com.cafe.crm.repositories.position;

import com.cafe.crm.models.user.Position;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;


public interface PositionRepository extends JpaRepository<Position, Long> {

	Position findByNameAndCompanyId(String name, Long companyId);

//	List<Position> findByIdInAndCompanyId(Long[] ids, Long companyId);

	List<Position> findByIsPositionUsePercentOfSalesIsTrueAndCompanyId(Long companyId);

	List<Position> findByCompanyId(Long companyId);

	List<Position> findByIdIn(Long[] ids);

}
