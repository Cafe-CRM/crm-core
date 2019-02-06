package com.cafe.crm.component;

import com.cafe.crm.configs.inteface.InfoConfig;
import com.cafe.crm.controllers.ClientCallRestController;
import com.cafe.crm.models.call.ClientCall;
import com.cafe.crm.services.interfaces.call.ClientCallService;
import com.cafe.crm.services.interfaces.voximplant.IPService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class ScheduleTasks {

    private final ClientCallService clientCallService;

    private final IPService ipService;

    private String managerNumber;

    private static Logger logger = LoggerFactory.getLogger(ClientCallRestController.class);

    public ScheduleTasks(ClientCallService clientCallService, IPService ipService, InfoConfig infoConfig) {
        this.clientCallService = clientCallService;
        this.ipService = ipService;
        managerNumber = infoConfig.getManagerNumber();
    }

    @Scheduled(cron = "30 * * * * *")
    private void getClientCall() {
        try {
            ClientCall clientCall = clientCallService.checkClientRequest();
            ipService.call(managerNumber, clientCall.getClientNumber(), clientCall.getId());
        } catch (NullPointerException e) {
            logger.info("Нет новых заявок");
        }
    }
}
