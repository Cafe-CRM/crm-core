package com.cafe.crm.services.impl.company.configuration.step;

import com.cafe.crm.configs.property.PriceNameProperties;
import com.cafe.crm.models.property.Property;
import com.cafe.crm.services.interfaces.company.configuration.ConfigurationStep;
import com.cafe.crm.services.interfaces.property.PropertyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

@Configuration
public class NextHoursPriceStep implements ConfigurationStep {

	private static final String NAME = "nextHoursPrice";
	private static final int PRIORITY = 81;

	private final PropertyService propertyService;
	private final PriceNameProperties priceNameProperties;

	@Autowired
	public NextHoursPriceStep(PropertyService propertyService, PriceNameProperties priceNameProperties) {
		this.propertyService = propertyService;
		this.priceNameProperties = priceNameProperties;
	}

	@Override
	public boolean isConfigured() {
		Property nextHoursProperty = propertyService.findByName(priceNameProperties.getNextHours());
		return nextHoursProperty != null;
	}

	@Override
	public String getStepName() {
		return NAME;
	}

	@Override
	public int getPriority() {
		return PRIORITY;
	}
}
