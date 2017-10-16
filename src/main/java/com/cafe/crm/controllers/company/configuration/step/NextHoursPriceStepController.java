package com.cafe.crm.controllers.company.configuration.step;

import com.cafe.crm.configs.property.PriceNameProperties;
import com.cafe.crm.models.property.Property;
import com.cafe.crm.services.interfaces.property.PropertyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping(value = "/company/configuration/step/next-hours-price")
public class NextHoursPriceStepController extends AbstractPriceStepController {

	@Autowired
	public NextHoursPriceStepController(PropertyService propertyService, PriceNameProperties priceNameProperties) {
		super(propertyService, priceNameProperties);
	}


	@RequestMapping(value = "/add", method = RequestMethod.POST)
	@ResponseBody
	public ResponseEntity<?> addProperty(String nextHours) {
		if (!isValidPriceValue(nextHours)) {
			return ResponseEntity.badRequest().body("Введено не допустимое значение!");
		}
		Property nextHoursProperty = new Property(getPriceNameProperties().getNextHours(), nextHours);
		getPropertyService().save(nextHoursProperty);

		return ResponseEntity.ok("");
	}

}
