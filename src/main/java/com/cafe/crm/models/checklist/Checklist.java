package com.cafe.crm.models.checklist;

import com.cafe.crm.models.BaseEntity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Objects;

@Entity
@Table(name = "checklist_for_shift")
public class Checklist extends BaseEntity {

	@Id
	@GeneratedValue
	private Long id;
	private String onCloseShiftText;
	private String onOpenShiftText;

	public Checklist() {
	}

	public Long getId() {
		return id;
	}

	public String getOnCloseShiftText() {
		return onCloseShiftText;
	}

	public void setOnCloseShiftText(String onCloseShiftText) {
		this.onCloseShiftText = onCloseShiftText;
	}

	public String getOnOpenShiftText() {
		return onOpenShiftText;
	}

	public void setOnOpenShiftText(String onOpenShiftText) {
		this.onOpenShiftText = onOpenShiftText;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		Checklist checklist = (Checklist) o;
		return Objects.equals(id, checklist.id) &&
				Objects.equals(onCloseShiftText, checklist.onCloseShiftText) &&
				Objects.equals(onOpenShiftText, checklist.onOpenShiftText);
	}

	@Override
	public int hashCode() {
		return Objects.hash(id, onCloseShiftText, onOpenShiftText);
	}

	@Override
	public String toString() {
		if (onCloseShiftText == null) {
			return 	"id=" + id +
					", onOpenShiftText= " + onOpenShiftText;
		} else {
			return 	"id=" + id +
					", onOpenShiftText= " + onCloseShiftText;
		}

	}
}
