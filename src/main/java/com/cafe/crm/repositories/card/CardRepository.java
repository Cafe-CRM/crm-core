package com.cafe.crm.repositories.card;

import com.cafe.crm.models.card.Card;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CardRepository extends JpaRepository<Card, Long> {

	@Query("SELECT u FROM Card u WHERE u.id =:id")
	Card getCardById(@Param("id") Long id);

	List<Card> findByCompanyId(Long companyId);

	Card findByEmailAndCompanyId(String email, Long companyId);

	Card findBySurnameAndCompanyId(String name, Long companyId);

	Card findByPhoneNumberAndCompanyId(String phone, Long companyId);

	Card findByNameAndSurnameAndCompanyId(String name, String surname, Long companyId);

	Card findBySurnameAndNameAndCompanyId(String surname, String name, Long companyId);

	@Query("SELECT u FROM Card u WHERE u.surname = :name AND u.company.id = :companyId")
	List<Card> findByListSurnameAndCompanyId(@Param("name") String name, @Param("companyId") Long companyId);

	List<Card> findByCompanyIdAndEmailNotNullAndAdvertisingIsTrue(Long companyId);

}

