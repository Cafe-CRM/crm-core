package com.cafe.crm.services.interfaces.client;

import com.cafe.crm.models.client.SettingClient;

import java.util.List;

public interface SettingClientService {
    List<SettingClient> getAll();

    SettingClient changeStatus(Long id, Boolean status);

    SettingClient getById(Long id);
}
