package com.cafe.crm.repositories.token;


import com.cafe.crm.models.token.ConfirmToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ConfirmTokenRepository extends JpaRepository<ConfirmToken, Long> {
	ConfirmToken findByCompanyId(Long companyId);
}
