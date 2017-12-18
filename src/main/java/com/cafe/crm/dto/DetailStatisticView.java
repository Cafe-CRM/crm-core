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

	private Set<UserSalaryDetail> userSalaryDetail;

	private Set<CalculateDTO> allCalculate;

	private double allSalaryCost;

	private double allOtherCost;

	private List<Cost> otherCost;

	private double repaidDebts;

	private double givenDebts;

	private double receiptsSum;

	public DetailStatisticView(LocalDate shiftDate, double cashBox, double allPrice, int clientsNumber,
							   List<UserDTO> usersOnShift, Set<UserSalaryDetail> userSalaryDetail,
							   Set<CalculateDTO> allCalculate, double allSalaryCost, double allOtherCost,
							   List<Cost> otherCost, double repaidDebts, double givenDebts, double receiptsSum) {
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
		this.repaidDebts = repaidDebts;
		this.givenDebts = givenDebts;
		this.receiptsSum = receiptsSum;
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

	public Set<UserSalaryDetail> getUserSalaryDetail() {
		return userSalaryDetail;
	}

	public void setUserSalaryDetail(Set<UserSalaryDetail> userSalaryDetail) {
		this.userSalaryDetail = userSalaryDetail;
	}

	public Set<CalculateDTO> getAllCalculate() {
		return allCalculate;
	}

	public void setAllCalculate(Set<CalculateDTO> allCalculate) {
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

	public double getRepaidDebts() {
		return repaidDebts;
	}

	public void setRepaidDebts(double repaidDebts) {
		this.repaidDebts = repaidDebts;
	}

	public double getGivenDebts() {
		return givenDebts;
	}

	public void setGivenDebts(double givenDebts) {
		this.givenDebts = givenDebts;
	}

	public double getReceiptsSum() {
		return receiptsSum;
	}

	public void setReceiptsSum(double receiptsSum) {
		this.receiptsSum = receiptsSum;
	}
}
