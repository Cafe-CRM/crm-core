package com.cafe.crm.configs;

import com.cafe.crm.configs.inteface.InfoConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

@Component
@PropertySource("file:./info.properties")
public class InfoConfigImpl implements InfoConfig {

    private String mailYandex;

    private String mailPassword;

    private String managerNumber;

    @Autowired
    public InfoConfigImpl(Environment environment) {
        mailYandex = environment.getRequiredProperty("mail.yandex");
        mailPassword = environment.getRequiredProperty("mail.password");
        managerNumber = environment.getRequiredProperty("manager.number");
    }

    public String getMailYandex() {
        return mailYandex;
    }

    public String getMailPassword() {
        return mailPassword;
    }

    public String getManagerNumber() {
        return managerNumber;
    }
}
