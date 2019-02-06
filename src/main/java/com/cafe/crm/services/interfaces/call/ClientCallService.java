package com.cafe.crm.services.interfaces.call;

import com.cafe.crm.models.call.ClientCall;

public interface ClientCallService {

    ClientCall checkClientRequest();

    ClientCall getById(Long id);

    void update(ClientCall clientCall);
}
