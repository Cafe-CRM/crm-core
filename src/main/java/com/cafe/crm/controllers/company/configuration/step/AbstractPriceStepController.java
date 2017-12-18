package com.cafe.crm.controllers.company.configuration.step;

import com.cafe.crm.configs.property.PriceNameProperties;
import com.cafe.crm.services.interfaces.property.PropertyService;
import org.apache.commons.lang3.math.NumberUtils;

public abstract class AbstractPriceStepController {

	private final PropertyService propertyService;
	private final PriceNameProperties priceNameProperties;

	public AbstractPriceStepController(PropertyService propertyService, PriceNameProperties priceNameProperties) {
		this.propertyService = propertyService;
		this.priceNameProperties = priceNameProperties;
	}

	protected PropertyService getPropertyService() {
		return propertyService;
	}

	protected PriceNameProperties getPriceNameProperties() {
		return priceNameProperties;
	}

	protected boolean isValidPriceValue(String value) {
		try {
			Double priceValueInDouble = NumberUtils.createDouble(value);
			if ((priceValueInDouble < 0d)) {
				return false;
			}
		} catch (NumberFormatException e) {
			return false;
		}
		return true;
	}
}
