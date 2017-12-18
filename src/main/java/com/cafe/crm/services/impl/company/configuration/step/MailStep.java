package com.cafe.crm.services.impl.company.configuration.step;

import com.cafe.crm.services.interfaces.company.configuration.ConfigurationStep;
import com.cafe.crm.services.interfaces.mail.MailSettingsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class MailStep implements ConfigurationStep {

	private static final String NAME = "mail";

	private final MailSettingsService mailSettingsService;

	@Autowired
	public MailStep(MailSettingsService mailSettingsService) {
		this.mailSettingsService = mailSettingsService;
	}

	@Override
	public boolean isConfigured() {
		return mailSettingsService.isExist();
	}

	@Override
	public String getStepName() {
		return NAME;
	}
}
