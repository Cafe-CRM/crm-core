package com.cafe.crm.dto;


import com.cafe.crm.models.client.Client;
import com.cafe.crm.models.client.Debt;
import com.cafe.crm.models.cost.Cost;
import com.cafe.crm.models.user.User;

import java.util.*;

public class TotalStatisticView {

	private double profit;
	private double alteredCashAmount;
	private double salariesCosts;
	private double otherCosts;
	private double profitRecalculation;
	private double lossRecalculation;
	private List<UserDTO> users;
	private Map<Client, ClientDetails> clientsOnDetails;
	private List<Cost> listOfOtherCosts;
	private List<Debt> givenOtherDebts;
	private List<Debt> repaidOtherDebts;
	private List<Debt> givenCashBoxDebts;
	private List<Debt> repaidCashBoxDebts;

	public TotalStatisticView(double profit, double alteredCashAmount, double salariesCosts, double otherCosts, double profitRecalculation,
							  double lossRecalculation, List<UserDTO> users,
							  Map<Client, ClientDetails> clientsOnDetails, List<Cost> listOfOtherCosts,
							  List<Debt> givenOtherDebts, List<Debt> repaidOtherDebts,
							  List<Debt> givenCashBoxDebts, List<Debt> repaidCashBoxDebts) {
		this.profit = profit;
		this.alteredCashAmount = alteredCashAmount;
		this.salariesCosts = salariesCosts;
		this.otherCosts = otherCosts;
		this.profitRecalculation = profitRecalculation;
		this.lossRecalculation = lossRecalculation;
		this.users = users;
		this.clientsOnDetails = clientsOnDetails;
		this.listOfOtherCosts = listOfOtherCosts;
		this.givenOtherDebts = givenOtherDebts;
		this.repaidOtherDebts = repaidOtherDebts;
		this.givenCashBoxDebts = givenCashBoxDebts;
		this.repaidCashBoxDebts = repaidCashBoxDebts;
	}

	public double getProfit() {
		return profit;
	}

	public void setProfit(double profit) {
		this.profit = profit;
	}

	public double getAlteredCashAmount() {
		return alteredCashAmount;
	}

	public void setAlteredCashAmount(double alteredCashAmount) {
		this.alteredCashAmount = alteredCashAmount;
	}

	public double getSalariesCosts() {
		return salariesCosts;
	}

	public void setSalariesCosts(double salariesCosts) {
		this.salariesCosts = salariesCosts;
	}

	public double getProfitRecalculation() {
		return profitRecalculation;
	}

	public void setProfitRecalculation(double profitRecalculation) {
		this.profitRecalculation = profitRecalculation;
	}

	public double getLossRecalculation() {
		return lossRecalculation;
	}

	public void setLossRecalculation(double lossRecalculation) {
		this.lossRecalculation = lossRecalculation;
	}

	public double getOtherCosts() {
		return otherCosts;
	}

	public void setOtherCosts(double otherCosts) {
		this.otherCosts = otherCosts;
	}

	public List<UserDTO> getUsers() {
		return users;
	}

	public void setUsers(List<UserDTO> users) {
		this.users = users;
	}

	public Map<Client, ClientDetails> getClientsOnDetails() {
		return clientsOnDetails;
	}

	public void setClientsOnDetails(Map<Client, ClientDetails> clientsOnDetails) {
		this.clientsOnDetails = clientsOnDetails;
	}

	public List<Debt> getGivenOtherDebts() {
		return givenOtherDebts;
	}

	public void setGivenOtherDebts(List<Debt> givenOtherDebts) {
		this.givenOtherDebts = givenOtherDebts;
	}

	public List<Debt> getRepaidOtherDebts() {
		return repaidOtherDebts;
	}

	public void setRepaidOtherDebts(List<Debt> repaidOtherDebts) {
		this.repaidOtherDebts = repaidOtherDebts;
	}

	public List<Debt> getGivenCashBoxDebts() {
		return givenCashBoxDebts;
	}

	public void setGivenCashBoxDebts(List<Debt> givenCashBoxDebts) {
		this.givenCashBoxDebts = givenCashBoxDebts;
	}

	public List<Debt> getRepaidCashBoxDebts() {
		return repaidCashBoxDebts;
	}

	public void setRepaidCashBoxDebts(List<Debt> repaidCashBoxDebts) {
		this.repaidCashBoxDebts = repaidCashBoxDebts;
	}

	public List<Cost> getListOfOtherCosts() {
		return listOfOtherCosts;
	}

	public void setListOfOtherCosts(List<Cost> listOfOtherCosts) {
		this.listOfOtherCosts = listOfOtherCosts;
	}
}