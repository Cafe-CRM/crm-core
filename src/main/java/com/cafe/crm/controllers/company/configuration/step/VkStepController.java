package com.cafe.crm.controllers.company.configuration.step;

import com.cafe.crm.configs.property.VkProperties;
import com.cafe.crm.models.property.Property;
import com.cafe.crm.services.interfaces.property.PropertyService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping(value = "/company/configuration/step/vk")
public class VkStepController {

	private final PropertyService propertyService;
	private final VkProperties generalVkProperties;

	@Value("${property.name.vk}")
	private String vkPropertyName;

	@Autowired
	public VkStepController(PropertyService propertyService, VkProperties generalVkProperties) {
		this.propertyService = propertyService;
		this.generalVkProperties = generalVkProperties;
	}

	@RequestMapping(value = "/add", method = RequestMethod.POST)
	@ResponseBody
	public ResponseEntity<?> addVk(VkProperties newVkProperties) throws JsonProcessingException {
		if (!isValidVkData(newVkProperties)) {
			return ResponseEntity.badRequest().body("Переданы недопустимые данные!");
		}
		String vkPropertyValueAsString = new ObjectMapper().writeValueAsString(newVkProperties);
		Property property = new Property(vkPropertyName, vkPropertyValueAsString);
		propertyService.save(property);
		VkProperties.copy(newVkProperties, generalVkProperties);

		return ResponseEntity.ok("");
	}

	private boolean isValidVkData(VkProperties vk) {
		return StringUtils.isNotBlank(vk.getAccessToken()) &&
				StringUtils.isNotBlank(vk.getApplicationId()) &&
				StringUtils.isNotBlank(vk.getChatId()) &&
				StringUtils.isNotBlank(vk.getMessageName()) &&
				StringUtils.isNotBlank(vk.getApiVersion());
	}
}
