package com.cafe.crm.controllers.company.configuration.step;

import com.cafe.crm.models.property.Property;
import com.cafe.crm.services.interfaces.property.PropertyService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping(value = "/company/configuration/step/masterKey")
public class MasterKeyStepController {

	private final PropertyService propertyService;
	private final PasswordEncoder passwordEncoder;

	@Value("${property.name.masterKey}")
	private String masterKeyPropertyName;

	@Autowired
	public MasterKeyStepController(PropertyService propertyService, PasswordEncoder passwordEncoder) {
		this.propertyService = propertyService;
		this.passwordEncoder = passwordEncoder;
	}

	@RequestMapping(value = "/add", method = RequestMethod.POST)
	@ResponseBody
	public ResponseEntity<?> addMasterKey(String masterKey) {
		if (StringUtils.isBlank(masterKey)) {
			return ResponseEntity.badRequest().body("Введен пустой мастер ключ!");
		}
		String encodedMasterKey = passwordEncoder.encode(masterKey.trim());
		Property property = new Property(masterKeyPropertyName, encodedMasterKey);
		propertyService.save(property);

		return ResponseEntity.ok("");
	}

}
