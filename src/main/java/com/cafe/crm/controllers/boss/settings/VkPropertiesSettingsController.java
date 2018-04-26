package com.cafe.crm.controllers.boss.settings;

import com.cafe.crm.configs.property.VkProperties;
import com.cafe.crm.models.property.Property;
import com.cafe.crm.services.interfaces.property.PropertyService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

@Controller
@RequestMapping("/boss/settings/vk-properties-setting")
public class VkPropertiesSettingsController {

	private final PropertyService propertyService;
	private final VkProperties generalVkProperties;

	private final org.slf4j.Logger logger = LoggerFactory.getLogger(VkPropertiesSettingsController.class);

	@Value("${property.name.vk}")
	private String vkPropertyName;

	@Autowired
	public VkPropertiesSettingsController(PropertyService propertyService, VkProperties generalVkProperties) {
		this.propertyService = propertyService;
		this.generalVkProperties = generalVkProperties;
	}

	@RequestMapping(method = RequestMethod.GET)
	public ModelAndView vkPropertiesSettingPage() throws IOException {
		ModelAndView modelAndView = new ModelAndView("settingPages/vkPropertiesSettingPage");
		modelAndView.addObject("vkProperties", getVkPropertiesFromDB());
		return modelAndView;
	}

	@RequestMapping(value = "/edit", method = RequestMethod.POST)
	public String editVkProperties(@ModelAttribute(value = "vkProperties") VkProperties newVkProperties,
								   HttpServletRequest request) throws JsonProcessingException {
		String vkPropertyValueAsString = new ObjectMapper().writeValueAsString(newVkProperties);
		Property property = propertyService.findByName(vkPropertyName);
		property.setValue(vkPropertyValueAsString);
		propertyService.save(property);
		VkProperties.copy(newVkProperties, generalVkProperties);

		logger.info("В настройках vk произошли изменения!");

		return "redirect:" + request.getHeader("Referer");
	}

	private VkProperties getVkPropertiesFromDB() throws IOException {
		ObjectMapper mapper = new ObjectMapper();
		Property property = propertyService.findByName(vkPropertyName);
		return mapper.readValue(property.getValue(), VkProperties.class);
	}
}
