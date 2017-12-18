package com.cafe.crm.repositories.board;

import com.cafe.crm.models.board.Board;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

import static org.springframework.data.jpa.domain.AbstractPersistable_.id;


public interface BoardRepository extends JpaRepository<Board, Long> {

	@Query("SELECT c FROM Board c WHERE c.isOpen = true AND c.company.id = :companyId")
	List<Board> getAllOpen(@Param("companyId") Long companyId);

	List<Board> findByCompanyId(Long companyId);

	Long countByCompanyIdAndIsOpenTrue(Long companyId);

}
