package com.cafe.crm.dto;


import com.cafe.crm.models.client.Calculate;
import com.cafe.crm.models.client.Client;
import com.cafe.crm.models.note.Note;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ShiftView {

	private List<UserDTO> usersOnShift;
	private List<Client> clients;
	private List<Calculate> activeCalculate;
	private Set<Calculate> allCalculate;
	private Map<Long, Integer> staffPercentBonuses;
	private double cashBox;
	private double bankCashBox;
	private double totalCashBox;
	private int usersTotalShiftSalary;
	private Double card;
	private Double allPrice;
	private LocalDate shiftDate;
	private double otherCosts;
	private List<Note> enabledNotes;

	public ShiftView(List<UserDTO> usersOnShift, List<Client> clients, List<Calculate> activeCalculate,
					 Set<Calculate> allCalculate, double cashBox, double totalCashBox, int usersTotalShiftSalary,
					 Double card, Double allPrice, LocalDate shiftDate, Double otherCosts, Double bankCashBox, List<Note> enabledNotes, Map<Long, Integer> userStaffPercentBonuses) {
		this.usersOnShift = usersOnShift;
		this.clients = clients;
		this.activeCalculate = activeCalculate;
		this.allCalculate = allCalculate;
		this.cashBox = cashBox;
		this.totalCashBox = totalCashBox;
		this.usersTotalShiftSalary = usersTotalShiftSalary;
		this.card = card;
		this.allPrice = allPrice;
		this.shiftDate = shiftDate;
		this.otherCosts = otherCosts;
		this.bankCashBox = bankCashBox;
		this.enabledNotes = enabledNotes;
		this.staffPercentBonuses = userStaffPercentBonuses;
	}

	public List<UserDTO> getUsersOnShift() {
		return usersOnShift;
	}

	public void setUsersOnShift(List<UserDTO> usersOnShift) {
		this.usersOnShift = usersOnShift;
	}

	public List<Client> getClients() {
		return clients;
	}

	public void setClients(List<Client> clients) {
		this.clients = clients;
	}

	public List<Calculate> getActiveCalculate() {
		return activeCalculate;
	}

	public void setActiveCalculate(List<Calculate> activeCalculate) {
		this.activeCalculate = activeCalculate;
	}

	public Set<Calculate> getAllCalculate() {
		return allCalculate;
	}

	public void setAllCalculate(Set<Calculate> allCalculate) {
		this.allCalculate = allCalculate;
	}

	public double getCashBox() {
		return cashBox;
	}

	public void setCashBox(double cashBox) {
		this.cashBox = cashBox;
	}

	public double getTotalCashBox() {
		return totalCashBox;
	}

	public void setTotalCashBox(double totalCashBox) {
		this.totalCashBox = totalCashBox;
	}

	public int getUsersTotalShiftSalary() {
		return usersTotalShiftSalary;
	}

	public void setUsersTotalShiftSalary(int usersTotalShiftSalary) {
		this.usersTotalShiftSalary = usersTotalShiftSalary;
	}

	public Double getCard() {
		return card;
	}

	public void setCard(Double card) {
		this.card = card;
	}

	public Double getAllPrice() {
		return allPrice;
	}

	public void setAllPrice(Double allPrice) {
		this.allPrice = allPrice;
	}

	public LocalDate getShiftDate() {
		return shiftDate;
	}

	public void setShiftDate(LocalDate shiftDate) {
		this.shiftDate = shiftDate;
	}

	public Double getOtherCosts() {
		return otherCosts;
	}

	public void setOtherCosts(Double otherCosts) {
		this.otherCosts = otherCosts;
	}

	public Double getBankCashBox() {
		return bankCashBox;
	}

	public void setBankCashBox(Double bankCashBox) {
		this.bankCashBox = bankCashBox;
	}

	public List<Note> getEnabledNotes() {
		return enabledNotes;
	}

	public void setEnabledNotes(List<Note> enabledNotes) {
		this.enabledNotes = enabledNotes;
	}

	public Map<Long, Integer> getStaffPercentBonuses() {
		return staffPercentBonuses;
	}

	public void setStaffPercentBonuses(Map<Long, Integer> staffPercentBonuses) {
		this.staffPercentBonuses = staffPercentBonuses;
	}
}
