package com.cafe.crm.dto;


import com.cafe.crm.models.board.Board;
import com.cafe.crm.models.card.Card;
import com.cafe.crm.models.client.Calculate;
import com.cafe.crm.models.client.Client;
import com.yc.easytransformer.annotations.Transform;

import java.util.List;

@Transform(Calculate.class)
public class CalculateDTO {

	private Long id;

	private String description;

	private boolean state = true;

	private boolean roundState = true;

	private boolean isPause = false;

	private List<Client> client;

	private Board board;

	private List<Card> cards;

	private List<String> dirtyOrder;

	private List<String> otherOrder;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public boolean isState() {
		return state;
	}

	public void setState(boolean state) {
		this.state = state;
	}

	public boolean isRoundState() {
		return roundState;
	}

	public void setRoundState(boolean roundState) {
		this.roundState = roundState;
	}

	public boolean isPause() {
		return isPause;
	}

	public void setPause(boolean pause) {
		isPause = pause;
	}

	public List<Client> getClient() {
		return client;
	}

	public void setClient(List<Client> client) {
		this.client = client;
	}

	public Board getBoard() {
		return board;
	}

	public void setBoard(Board board) {
		this.board = board;
	}

	public List<Card> getCards() {
		return cards;
	}

	public void setCards(List<Card> cards) {
		this.cards = cards;
	}

	public List<String> getDirtyOrder() {
		return dirtyOrder;
	}

	public void setDirtyOrder(List<String> dirtyOrder) {
		this.dirtyOrder = dirtyOrder;
	}

	public List<String> getOtherOrder() {
		return otherOrder;
	}

	public void setOtherOrder(List<String> otherOrder) {
		this.otherOrder = otherOrder;
	}
}
