package com.cafe.crm.dto;



public class ExtraUserData {

	private String oldPassword;
	private String newPassword;
	private String repeatedPassword;
	private String positionsIds;
	private String rolesIds;
	private String companyId;
	private boolean bossPasswordRequired;
	private String bossPassword;

	public ExtraUserData() {
	}

	public String getBossPassword() {
		return bossPassword;
	}

	public void setBossPassword(String bossPassword) {
		this.bossPassword = bossPassword;
	}

	public boolean isBossPasswordRequired() {
		return bossPasswordRequired;
	}

	public void setBossPasswordRequired(boolean bossPasswordRequired) {
		this.bossPasswordRequired = bossPasswordRequired;
	}

	public String getOldPassword() {
		return oldPassword;
	}

	public void setOldPassword(String oldPassword) {
		this.oldPassword = oldPassword;
	}

	public String getNewPassword() {
		return newPassword;
	}

	public void setNewPassword(String newPassword) {
		this.newPassword = newPassword;
	}

	public String getRepeatedPassword() {
		return repeatedPassword;
	}

	public void setRepeatedPassword(String repeatedPassword) {
		this.repeatedPassword = repeatedPassword;
	}

	public String getPositionsIds() {
		return positionsIds;
	}

	public void setPositionsIds(String positionsIds) {
		this.positionsIds = positionsIds;
	}

	public String getRolesIds() {
		return rolesIds;
	}

	public void setRolesIds(String rolesIds) {
		this.rolesIds = rolesIds;
	}

	public String getCompanyId() {
		return companyId;
	}

	public void setCompanyId(String companyId) {
		this.companyId = companyId;
	}
}
