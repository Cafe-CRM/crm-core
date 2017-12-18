package com.cafe.crm.services.impl.company.configuration.step;

import com.cafe.crm.models.property.Property;
import com.cafe.crm.services.interfaces.company.configuration.ConfigurationStep;
import com.cafe.crm.services.interfaces.property.PropertyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class VkStep implements ConfigurationStep {

	private static final String NAME = "vk";

	private final PropertyService propertyService;

	@Value("${property.name.vk}")
	private String vkPropertyName;

	@Autowired
	public VkStep(PropertyService propertyService) {
		this.propertyService = propertyService;
	}

	@Override
	public boolean isConfigured() {
		Property vkProperty = propertyService.findByName(vkPropertyName);
		return vkProperty != null;
	}

	@Override
	public String getStepName() {
		return NAME;
	}
}
