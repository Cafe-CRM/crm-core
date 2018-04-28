package com.cafe.crm.services.interfaces.calculate;

import com.cafe.crm.models.client.Calculate;

import java.util.List;

public interface CalculateService {

	Calculate save(Calculate calculate);

	void delete(Calculate calculate);

	List<Calculate> getAll();

	Calculate getOne(Long id);

	List<Calculate> getAllOpen();

	Calculate getAllOpenOnCalculate(Long calculateId);

	Calculate findByClientId(Long clientId);
}
