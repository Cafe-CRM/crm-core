package com.cafe.crm.dto;


import com.cafe.crm.models.client.Calculate;
import com.cafe.crm.models.cost.Cost;
import com.cafe.crm.models.shift.UserSalaryDetail;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class DetailStatisticView {

	private LocalDate shiftDate;

	private double cashBox;

	private double allPrice;

	private int clientsNumber;

	private List<UserDTO> usersOnShift;

	private List<UserSalaryDetail> userSalaryDetail;

	private Set<Calculate> allCalculate;

	private double allSalaryCost;

	private double allOtherCost;

	private List<Cost> otherCost;

	private double amountCredited;

	private double debts;

	public DetailStatisticView(LocalDate shiftDate, double cashBox, double allPrice, int clientsNumber,
							   List<UserDTO> usersOnShift, List<UserSalaryDetail> userSalaryDetail,
							   Set<Calculate> allCalculate, double allSalaryCost, double allOtherCost,
							   List<Cost> otherCost, double amountCredited, double debts) {
		this.shiftDate = shiftDate;
		this.cashBox = cashBox;
		this.allPrice = allPrice;
		this.clientsNumber = clientsNumber;
		this.usersOnShift = usersOnShift;
		this.userSalaryDetail = userSalaryDetail;
		this.allCalculate = allCalculate;
		this.allSalaryCost = allSalaryCost;
		this.allOtherCost = allOtherCost;
		this.otherCost = otherCost;
		this.amountCredited = amountCredited;
		this.debts = debts;
	}

	public LocalDate getShiftDate() {
		return shiftDate;
	}

	public void setShiftDate(LocalDate shiftDate) {
		this.shiftDate = shiftDate;
	}

	public double getCashBox() {
		return cashBox;
	}

	public void setCashBox(double cashBox) {
		this.cashBox = cashBox;
	}

	public double getAllPrice() {
		return allPrice;
	}

	public void setAllPrice(double allPrice) {
		this.allPrice = allPrice;
	}

	public int getClientsNumber() {
		return clientsNumber;
	}

	public void setClientsNumber(int clientsNumber) {
		this.clientsNumber = clientsNumber;
	}

	public List<UserDTO> getUsersOnShift() {
		return usersOnShift;
	}

	public void setUsersOnShift(List<UserDTO> usersOnShift) {
		this.usersOnShift = usersOnShift;
	}

	public List<UserSalaryDetail> getUserSalaryDetail() {
		return userSalaryDetail;
	}

	public void setUserSalaryDetail(List<UserSalaryDetail> userSalaryDetail) {
		this.userSalaryDetail = userSalaryDetail;
	}

	public Set<Calculate> getAllCalculate() {
		return allCalculate;
	}

	public void setAllCalculate(Set<Calculate> allCalculate) {
		this.allCalculate = allCalculate;
	}

	public double getAllSalaryCost() {
		return allSalaryCost;
	}

	public void setAllSalaryCost(double allSalaryCost) {
		this.allSalaryCost = allSalaryCost;
	}

	public double getAllOtherCost() {
		return allOtherCost;
	}

	public void setAllOtherCost(double allOtherCost) {
		this.allOtherCost = allOtherCost;
	}

	public List<Cost> getOtherCost() {
		return otherCost;
	}

	public void setOtherCost(List<Cost> otherCost) {
		this.otherCost = otherCost;
	}

	public double getAmountCredited() {
		return amountCredited;
	}

	public void setAmountCredited(double amountCredited) {
		this.amountCredited = amountCredited;
	}

	public double getDebts() {
		return debts;
	}

	public void setDebts(double debts) {
		this.debts = debts;
	}
}
