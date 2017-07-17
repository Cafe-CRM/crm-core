package com.cafe.crm.services.impl.client;

import com.cafe.crm.models.card.Card;
import com.cafe.crm.models.client.Client;
import com.cafe.crm.repositories.client.ClientRepository;
import com.cafe.crm.services.interfaces.client.ClientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Service
public class ClientServiceImpl implements ClientService {

	@Autowired
	private ClientRepository clientRepository;

	@Override
	public void save(Client client) {
		clientRepository.saveAndFlush(client);
	}

	@Override
	public void saveAll(List<Client> clients) {
		clientRepository.save(clients);
	}

	@Override
	public void delete(Client client) {
		clientRepository.delete(client);
	}

	@Override
	public List<Client> getAll() {
		return clientRepository.findAll();
	}

	@Override
	public Client getOne(Long id) {
		return clientRepository.findOne(id);
	}

	@Override
	public List<Client> getAllOpen() {
		return clientRepository.getAllOpen();
	}

	@Override
	public List<Client> findByIdIn(long[] ids) {
		return clientRepository.findByIdIn(ids);
	}

	@Override
	public List<Client> findByCardId(Long cardId) {
		return clientRepository.findByCardId(cardId);
	}

	@Override
	public Set<Card> findCardByClientIdIn(long[] clientsIds) {
		return clientRepository.findCardByClientIdIn(clientsIds);
	}

	@Transactional
	@Override
	public boolean updateClientTime(Long id, int hours, int minutes) {
		Client client = getOne(id);
		if (client == null) {
			return false;
		}
		LocalDateTime timeStart = client.getTimeStart();
		LocalDateTime newTimeStart = timeStart.withHour(hours).withMinute(minutes);
		client.setTimeStart(newTimeStart);
		save(client);
		return true;
	}

}
