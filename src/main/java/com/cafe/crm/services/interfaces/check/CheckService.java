package com.cafe.crm.services.interfaces.check;

import com.cafe.crm.models.client.Client;

import java.util.List;

public interface CheckService {
    void printCheck(List<Client> clients);

    void printCheckWithCostPrice(List<Client> clients, List<Client> costPriceClients);

    void printCheckWithNewAmount(List<Client> clients, Double modifiedAmount);
}
