package com.cafe.crm.services.impl.company.configuration.step;

import com.cafe.crm.models.property.Property;
import com.cafe.crm.services.interfaces.company.configuration.ConfigurationStep;
import com.cafe.crm.services.interfaces.property.PropertyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class MasterKeyStep implements ConfigurationStep {

	private static final String NAME = "masterKey";

	private final PropertyService propertyService;

	@Value("${property.name.masterKey}")
	private String masterKeyPropertyName;


	@Autowired
	public MasterKeyStep(PropertyService propertyService) {
		this.propertyService = propertyService;
	}

	@Override
	public boolean isConfigured() {
		Property masterKeyProperty = propertyService.findByName(masterKeyPropertyName);
		return masterKeyProperty != null;
	}

	@Override
	public String getStepName() {
		return NAME;
	}

}
