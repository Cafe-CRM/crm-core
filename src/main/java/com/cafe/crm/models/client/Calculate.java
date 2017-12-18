package com.cafe.crm.models.client;

import com.cafe.crm.dto.CalculateDTO;
import com.cafe.crm.models.BaseEntity;
import com.cafe.crm.models.board.Board;
import com.cafe.crm.models.card.Card;
import com.yc.easytransformer.annotations.NotTransform;
import com.yc.easytransformer.annotations.Transform;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;

@Entity
@Table(name = "calculation")
@Transform(CalculateDTO.class)
public class Calculate extends BaseEntity {
	@Id
	@GeneratedValue
	private Long id;

	@NotNull
	@Size(max = 30)
	private String description;

	private boolean state = true; // Open or Closed

	private boolean isPause = false;  // now is paused ?

	@OneToMany(fetch = FetchType.EAGER)
	private List<Client> client;

	@ManyToOne
	private Board board;

	@ManyToMany
	@NotTransform
	private List<Card> cards;

	public Calculate() {
	}

	public boolean isPause() {
		return isPause;
	}

	public void setPause(boolean pause) {
		isPause = pause;
	}

	public List<Card> getCards() {
		return cards;
	}

	public void setCards(List<Card> cards) {
		this.cards = cards;
	}

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

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		Calculate calculate = (Calculate) o;

		if (id != null ? !id.equals(calculate.id) : calculate.id != null) return false;
		return description != null ? description.equals(calculate.description) : calculate.description == null;
	}

	@Override
	public int hashCode() {
		int result = id != null ? id.hashCode() : 0;
		result = 31 * result + (description != null ? description.hashCode() : 0);
		return result;
	}
}
