package com.cafe.crm.services.interfaces.voximplant;

public interface IPService {

    void call(String from, String to, Long callId);

    String getVoximplantLoginForWebCall();

    String getVoximplantPasswordForWebCall();

    String getVoximplantUserLogin(String fullLogin);

    String getVoximplantCodeToSetRecord();

}
