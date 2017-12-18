package com.cafe.crm.services.interfaces.calculate;

import com.cafe.crm.models.client.Client;

import java.util.List;

public interface CalculateControllerService {

	void createCalculate(Long id, Long number, String descr);

	void createCalculateWithCard(Long id, Long number, String descr, Long idCard);

	void refreshBoard(Long idC, Long idB);

	void addClient(Long id, Long number, String descr);

	List<Client> calculatePrice();

	List<Client> calculatePrice(Long calculateId);

	List<Client> outputClients(long[] clientsId);

	void closeClient(long[] clientsId, Long calculateId);

	void closeNewSumClient(Double modifiedAmount, String password, long[] clientsId, Long calculateId);

	void closeClientDebt(String debtorName, long[] clientsId, Long calculateId, Double amountOfDebt);

	Long addCardOnClient(Long calculateId, Long clientId, Long cardId);

	void deleteClients(long[] clientsId, Long calculateId);

	void deleteCalculate(String password, Long calculateId);

	void pauseClient(Long clientId);

	void unpauseClient(Long clientId);

}
