package com.cafe.crm.models.login;


import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

@Entity
@Table(name = "login_data")
public class LoginData {

	@Id
	private String remoteAddress;

	private int errorCount;

	private Date lastLoginDate;

	public LoginData() {}

	public LoginData(String remoteAddress, int errorCount, Date lastLoginDate) {
		this.remoteAddress = remoteAddress;
		this.errorCount = errorCount;
		this.lastLoginDate = lastLoginDate;
	}

	public String getRemoteAddress() {
		return remoteAddress;
	}

	public void setRemoteAddress(String remoteAddress) {
		this.remoteAddress = remoteAddress;
	}

	public int getErrorCount() {
		return errorCount;
	}

	public void setErrorCount(int errorCount) {
		this.errorCount = errorCount;
	}

	public Date getLastLoginDate() {
		return lastLoginDate;
	}

	public void setLastLoginDate(Date lastLoginDate) {
		this.lastLoginDate = lastLoginDate;
	}
}
