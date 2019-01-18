package com.cafe.crm.repositories.client;

import com.cafe.crm.models.card.Card;
import com.cafe.crm.models.client.Client;
import com.cafe.crm.models.client.TimerOfPause;
import com.cafe.crm.repositories.customRepository.CommonRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

public interface ClientRepository extends JpaRepository<Client, Long> {

	@Query("SELECT c FROM Client c where c.state = true AND c.deleteState = false AND c.company.id = :companyId")
	List<Client> getAllOpenAndCompanyId(@Param("companyId") Long companyId);

	@Query("SELECT u FROM Client u WHERE u.company.id = :companyId AND u.id = (select max(id) from Client)")
	Client getLastAndCompanyId(@Param ("companyId") Long companyId);

	List<Client> findByIdIn(long[] ids);

	List<Client> findByCardId(Long cardId);

	@Query("SELECT c.card FROM Client c WHERE c.id IN ?1 ")
	Set<Card> findCardByClientIdIn(long[] clientsIds);

	List<Client> findByCompanyId(Long companyId);

	@Query(value =  "SELECT * FROM clients WHERE (clients.time_start BETWEEN :startDate AND :endDate " +
			"OR addtime(clients.time_start, clients.passed_time) BETWEEN :startDate AND :endDate) AND clients.company_id = :companyId", nativeQuery = true)
//	@Query(value =  "select id from clients where clients.company_id = :companyId", nativeQuery = true)
	List<Client> findByDatesAndCompanyId(
			@Param("startDate") LocalDateTime startDate,
			@Param("endDate") LocalDateTime endDate,
			@Param("companyId") Long companyId
	);
}
