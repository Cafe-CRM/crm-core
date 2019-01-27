package com.cafe.crm.controllers.boss.settings;

import com.cafe.crm.models.client.SettingClient;
import com.cafe.crm.services.interfaces.client.SettingClientService;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;


@Controller
@RequestMapping(value = "/boss/settings/close-client")
public class SettingClientController {

    private final SettingClientService settingClientService;
    private final org.slf4j.Logger logger = LoggerFactory.getLogger(SettingClientController.class);

    @Autowired
    public SettingClientController(SettingClientService settingClientService) {
        this.settingClientService = settingClientService;
    }


    @RequestMapping(method = RequestMethod.GET)
    public String showClientSettingPage(Model model) {
        model.addAttribute("clientSetting", settingClientService.getAll());
        return "settingPages/clientSetting";
    }

    @RequestMapping(path = "/changeStatus", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<?> changeStatusClientSetting(@RequestParam("id") Long id,
                                              @RequestParam("enable") String enable) {
        boolean status = !Boolean.valueOf(enable);
        String state = status ? "enabled" : "disabled";
        SettingClient settingClient = settingClientService.changeStatus(id, status);

        logger.info("Изменён статус расчета" +
                "\" на " + state);

        return ResponseEntity.ok("");
    }

    @RequestMapping(value = "/get-setting-client", method = RequestMethod.GET)
    public ResponseEntity<SettingClient> getSettingClient() {
        return ResponseEntity.ok(settingClientService.getById((long) 1));
    }

}
