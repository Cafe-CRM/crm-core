package com.cafe.crm.services.interfaces.company.configuration;


public interface ConfigurationStep {

	boolean isConfigured();

	String getStepName();

	default int getPriority() {
		return 100;
	}

}
