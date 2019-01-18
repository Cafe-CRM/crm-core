package com.cafe.crm.dto;


import java.util.Map;

public class ShiftCloseDTO {
	private Map<Long, Integer> mapOfUsersIdsAndBonuses;
	private Double cashBox;
	private Double bankCashBox;
	private String comment;
	private Map<String, String> mapOfNoteNameAndValue;

	public Map<Long, Integer> getMapOfUsersIdsAndBonuses() {
		return mapOfUsersIdsAndBonuses;
	}

	public void setMapOfUsersIdsAndBonuses(Map<Long, Integer> mapOfUsersIdsAndBonuses) {
		this.mapOfUsersIdsAndBonuses = mapOfUsersIdsAndBonuses;
	}

	public Double getCashBox() {
		return cashBox;
	}

	public void setCashBox(Double cashBox) {
		this.cashBox = cashBox;
	}

	public Double getBankCashBox() {
		return bankCashBox;
	}

	public void setBankCashBox(Double bankCashBox) {
		this.bankCashBox = bankCashBox;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public Map<String, String> getMapOfNoteNameAndValue() {
		return mapOfNoteNameAndValue;
	}

	public void setMapOfNoteNameAndValue(Map<String, String> mapOfNoteNameAndValue) {
		this.mapOfNoteNameAndValue = mapOfNoteNameAndValue;
	}

	@Override
	public String toString() {
		return "ShiftCloseDTO{" +
				"mapOfUsersIdsAndBonuses=" + mapOfUsersIdsAndBonuses +
				", cashBox=" + cashBox +
				", bankCashBox=" + bankCashBox +
				", comment='" + comment + '\'' +
				", mapOfNoteNameAndValue=" + mapOfNoteNameAndValue +
				'}';
	}
}