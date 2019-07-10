package com.cafe.crm.services.interfaces.calculate;

import com.cafe.crm.models.client.Client;

import java.time.LocalTime;


public interface CalculatePriceService {

	void payWithCardAndCache(Client client);

	void calculatePriceTime(Client client, LocalTime timeNow);

	void getAllPrice(Client client);

	void addDiscountOnPriceTime(Client client);

	void calculatePriceTimeIfWasPause(Client client, LocalTime timeNow);

}
