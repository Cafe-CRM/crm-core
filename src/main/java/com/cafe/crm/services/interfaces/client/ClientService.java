package com.cafe.crm.services.interfaces.client;


import com.cafe.crm.models.card.Card;
import com.cafe.crm.models.client.Client;

import java.util.List;
import java.util.Set;

public interface ClientService {

	void save(Client client);

	void saveAll(List<Client> clients);

	void delete(Client client);

	List<Client> getAll();

	Client getOne(Long id);

	List<Client> getAllOpen();

	List<Client> findByIdIn(long[] ids);

	List<Client> findByCardId(Long cardId);

	Set<Card> findCardByClientIdIn(long[] clientsIds);

	Client getLast();

	boolean updateClientTime(Long id, int hours, int minutes);

}
