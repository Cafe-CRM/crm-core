package com.cafe.crm.controllers.boss.settings;

import com.cafe.crm.models.property.Property;
import com.cafe.crm.services.interfaces.property.PropertyService;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;


@Controller
@RequestMapping(value = "/boss/settings/check-setting")
public class CheckSettingController {

    private final PropertyService propertyService;
    private final org.slf4j.Logger logger = LoggerFactory.getLogger(CheckSettingController.class);

    @Value("${property.name.check.propertyName}")
    private String checkPropertyName;

    @Autowired
    public CheckSettingController(PropertyService propertyService) {
        this.propertyService = propertyService;
    }


    @RequestMapping(method = RequestMethod.GET)
    public String showClientSettingPage(Model model) {
        model.addAttribute("checkSetting", propertyService.findByName(checkPropertyName));
        return "settingPages/checkSettingPage";
    }

    @RequestMapping(path = "/changeStatus", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<?> changeStatusClientSetting() {

        Property checkProperty = propertyService.findByName(checkPropertyName);
        Boolean newCheckStatus = !Boolean.valueOf(checkProperty.getValue());
        checkProperty.setValue(newCheckStatus.toString());
        propertyService.save(checkProperty);

        logger.info("Смена статуса печати чека на " + newCheckStatus);

        return ResponseEntity.ok("");
    }

}
