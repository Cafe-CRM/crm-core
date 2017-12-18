package com.cafe.crm.repositories.client;

import com.cafe.crm.models.card.Card;
import com.cafe.crm.models.client.Client;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

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
}
