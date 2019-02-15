package com.cafe.crm.services.interfaces.check;

public interface PrintCheckService {
    void printCheck(long[] clientsId);

    void printCheckWithCostPrice(long[] clientsId, long[] costPriceClientsId);

    void repeatPrintCheck(Long calculateId);
}
