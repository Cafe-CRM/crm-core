package com.cafe.crm.services.impl.company.configuration;


import com.cafe.crm.exceptions.company.configuration.StepByStepConfigurationException;
import com.cafe.crm.services.interfaces.company.configuration.ConfigurationStep;
import com.cafe.crm.services.interfaces.company.configuration.StepByStepConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;

@Component
public class StepByStepConfigurationImpl implements StepByStepConfiguration {

	private final List<ConfigurationStep> steps;

	@Autowired
	public StepByStepConfigurationImpl(List<ConfigurationStep> steps) {
		steps.sort(new ConfigurationStepComparator());
		this.steps = steps;
	}

	@Override
	public boolean hasNextStep() {
		return getNotConfiguredStepOrNull() != null;
	}

	@Override
	public ConfigurationStep getNextStep() {
		ConfigurationStep configurationStep = getNotConfiguredStepOrNull();
		if (Objects.isNull(configurationStep)) {
			throw new StepByStepConfigurationException("Некорректный вызов метода!");
		}
		return configurationStep;
	}

	private ConfigurationStep getNotConfiguredStepOrNull() {
		for (ConfigurationStep step : steps) {
			if (!step.isConfigured()) {
				return step;
			}
		}
		return null;
	}

	private static class ConfigurationStepComparator implements Comparator<ConfigurationStep> {
		@Override
		public int compare(ConfigurationStep step1, ConfigurationStep step2) {
			return step1.getPriority() - step2.getPriority();
		}
	}

}
