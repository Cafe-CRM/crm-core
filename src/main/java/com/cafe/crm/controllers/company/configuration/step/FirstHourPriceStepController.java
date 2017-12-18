package com.cafe.crm.controllers.company.configuration.step;

import com.cafe.crm.configs.property.PriceNameProperties;
import com.cafe.crm.models.property.Property;
import com.cafe.crm.services.interfaces.property.PropertyService;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping(value = "/company/configuration/step/first-hour-price")
public class FirstHourPriceStepController extends AbstractPriceStepController {

	@Autowired
	public FirstHourPriceStepController(PropertyService propertyService, PriceNameProperties priceNameProperties) {
		super(propertyService, priceNameProperties);
	}


	@RequestMapping(value = "/add", method = RequestMethod.POST)
	@ResponseBody
	public ResponseEntity<?> addProperty(String firstHour) {
		if (!isValidPriceValue(firstHour)) {
			return ResponseEntity.badRequest().body("Введено не допустимое значение!");
		}
		Property firstHourProperty = new Property(getPriceNameProperties().getFirstHour(), firstHour);
		getPropertyService().save(firstHourProperty);

		return ResponseEntity.ok("");
	}

}
