package com.cafe.crm.repositories.call;

import com.cafe.crm.models.call.ClientCall;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClientCallRepository extends JpaRepository<ClientCall, Long> {
}
