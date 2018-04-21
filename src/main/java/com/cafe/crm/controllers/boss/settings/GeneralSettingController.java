package com.cafe.crm.controllers.boss.settings;

import com.cafe.crm.dto.PropertyWrapper;
import com.cafe.crm.models.property.Property;
import com.cafe.crm.services.interfaces.property.PropertyService;
import com.cafe.crm.utils.TimeManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/boss/settings/general-setting")
public class GeneralSettingController {

	private final TimeManager timeManager;

	@Autowired
	public GeneralSettingController(TimeManager timeManager, PropertyService propertyService) {
		this.timeManager = timeManager;
	}

	@PostMapping("/get-server-time-date")
	@ResponseBody
	public List<Object> getServerTimeDate() {
		List<Object> list = new ArrayList<>();
		list.add(timeManager.getDateTime());
		list.add(timeManager.getIsServerDateTime());
		return list;
	}
}

