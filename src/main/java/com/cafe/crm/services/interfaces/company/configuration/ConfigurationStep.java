package com.cafe.crm.services.interfaces.company.configuration;


public interface ConfigurationStep<T> {

	boolean isConfigured();

	String getStepName();

	default void setIsReconfigured(boolean value) {}

	default boolean isIsReconfigured() {
		return false;
	}

	default String getAttrName() {
		return "";
	}

	default T getAttribute() {
		return null;
	}

	default int getPriority() {
		return 100;
	}

}
