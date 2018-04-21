package com.cafe.crm.controllers.boss.settings;

import com.cafe.crm.configs.property.PriceNameProperties;
import com.cafe.crm.models.property.Property;
import com.cafe.crm.services.interfaces.property.PropertyService;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

@Controller
@RequestMapping(value = "/boss/settings/price-setting")
public class PriceSettingsController {

	private final PropertyService propertyService;
	private final PriceNameProperties priceNameProperties;

	private final org.slf4j.Logger logger = LoggerFactory.getLogger(PriceSettingsController.class);

	@Autowired
	public PriceSettingsController(PropertyService propertyService, PriceNameProperties priceNameProperties) {
		this.propertyService = propertyService;
		this.priceNameProperties = priceNameProperties;
	}

	@RequestMapping(method = RequestMethod.GET)
	public String showPriceSettingPage(Model model) {
		model.addAttribute("properties", propertyService.findByNameIn(priceNameProperties.getFirstHour(),
				priceNameProperties.getNextHours(), priceNameProperties.getRefBonus()));

		return "settingPages/priceSettingsPage";
	}

	@RequestMapping(value = "/edit", method = RequestMethod.POST)
	@ResponseBody
	public ResponseEntity<?> editProperty(@Valid Property property, BindingResult bindingResult, HttpServletRequest request) {
		if (bindingResult.hasErrors()) {
			String fieldError = bindingResult.getFieldError().getDefaultMessage();
			return ResponseEntity.badRequest().body(fieldError);
		}
		String value = propertyService.getOne(property.getId()).getValue();
		Property savedProp = propertyService.save(property);

		logger.info("Настройка цены с id: " + savedProp.getId() + " и названием: \"" + savedProp.getName() +
					"\" была изменена. Цена: " + value + " -> " + savedProp.getValue());

		return ResponseEntity.ok("");
	}
}
