package com.cafe.crm.services.interfaces.check;

public interface PrintCheckService {
    void printCheck(long[] clientsId);

    void repeatPrintCheck(Long calculateId);
}
