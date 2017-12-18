package com.cafe.crm.repositories.mail;


import com.cafe.crm.models.mail.MailSettings;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MailSettingsRepository extends JpaRepository<MailSettings, Long> {

	MailSettings findByEmailIgnoreCaseAndCompanyId(String senderMail, Long companyId);

	List<MailSettings> findByCompanyId (Long companyId);

	Long countByCompanyId(Long companyId);
}
