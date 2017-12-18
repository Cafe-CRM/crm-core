package com.cafe.crm.models.board;

import com.cafe.crm.models.BaseEntity;
import com.yc.easytransformer.annotations.Transform;
import com.cafe.crm.utils.JsonField;
import com.fasterxml.jackson.annotation.JsonView;
import org.hibernate.validator.constraints.NotBlank;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "board")
@Transform(Board.class)
public class Board extends BaseEntity {
	@Id
	@GeneratedValue
	@JsonView(JsonField.Board.class)
	private Long id;

	@NotBlank(message = "Название не должно быть пустым!")
	@JsonView(JsonField.Board.class)
	private String name;

	@NotNull
	@JsonView(JsonField.Board.class)
	private Boolean isOpen = true;

	public Board() {
	}

	public Boolean getIsOpen() {
		return isOpen;
	}

	public void setIsOpen(Boolean isOpen) {
		this.isOpen = isOpen;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		Board board = (Board) o;

		if (id != null ? !id.equals(board.id) : board.id != null) return false;
		return name != null ? name.equals(board.name) : board.name == null;
	}

	@Override
	public int hashCode() {
		int result = id != null ? id.hashCode() : 0;
		result = 31 * result + (name != null ? name.hashCode() : 0);
		return result;
	}
}
