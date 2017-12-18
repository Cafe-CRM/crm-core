package com.cafe.crm.services.interfaces.company.configuration;


public interface StepByStepConfiguration {

	boolean hasNextStep();

	ConfigurationStep getNextStep();

}
