package com.cafe.crm.models.note;

import com.cafe.crm.models.BaseEntity;
import com.cafe.crm.models.shift.Shift;

import javax.persistence.*;

@Entity
public class NoteRecord extends BaseEntity {

	@Id
	@GeneratedValue
	private Long id;

	private String name;

	private String value;

	@ManyToOne
	@JoinColumn(name = "shift_id", nullable = false)
	private Shift shift;

	public Shift getShift() {
		return shift;
	}

	public void setShift(Shift shift) {
		this.shift = shift;
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

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}
}
