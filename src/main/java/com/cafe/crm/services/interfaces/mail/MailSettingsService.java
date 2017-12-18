package com.cafe.crm.services.interfaces.mail;


import com.cafe.crm.models.mail.MailSettings;

import java.util.List;

public interface MailSettingsService {

	void save(MailSettings settings);

	void delete(MailSettings settings);

	void delete(Long id);

	MailSettings get(Long id);

	MailSettings findByEmail(String email);

	List<MailSettings> getAll();

	boolean isExist();
}
