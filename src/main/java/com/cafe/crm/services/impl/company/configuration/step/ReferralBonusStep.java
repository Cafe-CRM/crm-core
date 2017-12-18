package com.cafe.crm.services.impl.company.configuration.step;

import com.cafe.crm.configs.property.PriceNameProperties;
import com.cafe.crm.models.property.Property;
import com.cafe.crm.services.interfaces.company.configuration.ConfigurationStep;
import com.cafe.crm.services.interfaces.property.PropertyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ReferralBonusStep implements ConfigurationStep {

	private static final String NAME = "referralBonus";
	private static final int PRIORITY = 82;

	private final PropertyService propertyService;
	private final PriceNameProperties priceNameProperties;

	@Autowired
	public ReferralBonusStep(PropertyService propertyService, PriceNameProperties priceNameProperties) {
		this.propertyService = propertyService;
		this.priceNameProperties = priceNameProperties;
	}

	@Override
	public boolean isConfigured() {
		Property refBonusProperty = propertyService.findByName(priceNameProperties.getRefBonus());
		return refBonusProperty != null;
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
