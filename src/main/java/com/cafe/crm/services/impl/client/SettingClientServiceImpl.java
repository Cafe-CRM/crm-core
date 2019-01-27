package com.cafe.crm.services.impl.client;

import com.cafe.crm.models.client.SettingClient;
import com.cafe.crm.repositories.client.SettingClientRepository;
import com.cafe.crm.services.interfaces.client.ClientService;
import com.cafe.crm.services.interfaces.client.SettingClientService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SettingClientServiceImpl implements SettingClientService {

    private final SettingClientRepository settingClientRepository;

    public SettingClientServiceImpl(SettingClientRepository settingClientRepository) {
        this.settingClientRepository = settingClientRepository;
    }


    public SettingClient changeStatus(Long id, Boolean status) {
        SettingClient settingClient = settingClientRepository.findOne(id);
        if(settingClient != null) {
            settingClient.setEnabled(status);
            settingClientRepository.save(settingClient);
        }
        return settingClient;
    }

    public List<SettingClient> getAll() {
        return settingClientRepository.findAll();
    }

    public SettingClient getById(Long id) {
        return settingClientRepository.getOne(id);
    }
}
