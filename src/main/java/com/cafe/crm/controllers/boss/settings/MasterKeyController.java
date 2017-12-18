package com.cafe.crm.controllers.boss.settings;

import com.cafe.crm.exceptions.user.UserDataException;
import com.cafe.crm.models.property.Property;
import com.cafe.crm.services.interfaces.property.PropertyService;
import com.cafe.crm.services.interfaces.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@Controller
@RequestMapping("/boss/settings/masterKey")
public class MasterKeyController {

	private final PropertyService propertyService;
	private final UserService userService;
	private final PasswordEncoder passwordEncoder;

	@Value("${property.name.masterKey}")
	private String masterKeyPropertyName;

	@Autowired
	public MasterKeyController(PropertyService propertyService, UserService userService, PasswordEncoder passwordEncoder) {
		this.propertyService = propertyService;
		this.userService = userService;
		this.passwordEncoder = passwordEncoder;
	}

	@RequestMapping(method = RequestMethod.GET)
	public String showMasterKeySettingsPage() {
		return "settingPages/masterKeySettingsPage";
	}

	@RequestMapping(value = "/editMasterKey", method = RequestMethod.POST)
	@ResponseBody
	public ResponseEntity editMasterKey(@RequestParam(name = "old") String oldPassword,
										@RequestParam(name = "new") String newMasterKey,
										Principal principal) {
		if (userService.isValidPassword(principal.getName(), oldPassword)) {
			Property masterKeyProperty = propertyService.findByName(masterKeyPropertyName);
			String encodedMasterKey = passwordEncoder.encode(newMasterKey);
			masterKeyProperty.setValue(encodedMasterKey);
			propertyService.save(masterKeyProperty);
			return ResponseEntity.ok("Мастер ключ успешно добавлен!");
		} else {
			throw new UserDataException("Не получилось сохранить новый мастер - ключ!");
		}
	}

	@ExceptionHandler(value = UserDataException.class)
	public ResponseEntity<?> handleUserUpdateException(UserDataException ex) {
		return ResponseEntity.badRequest().body(ex.getMessage());
	}
}
