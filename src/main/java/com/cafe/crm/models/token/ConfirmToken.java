package com.cafe.crm.models.token;

import com.cafe.crm.models.BaseEntity;
import org.hibernate.validator.constraints.NotBlank;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "tokens")
public class ConfirmToken extends BaseEntity {
	@Id
	@GeneratedValue
	private Long id;

	@NotBlank
	private String token;

	@Column(name = "start_time")
	private Date startTime;

	public ConfirmToken(String token, Date startTime) {
		this.token = token;
		this.startTime = startTime;
	};

	public ConfirmToken() {};

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public Date getStartTime() {
		return startTime;
	}

	public void setStartTime(Date startTime) {
		this.startTime = startTime;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		ConfirmToken that = (ConfirmToken) o;

		if (id != null ? !id.equals(that.id) : that.id != null) return false;
		if (token != null ? !token.equals(that.token) : that.token != null) return false;
		return startTime != null ? startTime.equals(that.startTime) : that.startTime == null;
	}

	@Override
	public int hashCode() {
		int result = id != null ? id.hashCode() : 0;
		result = 31 * result + (token != null ? token.hashCode() : 0);
		result = 31 * result + (startTime != null ? startTime.hashCode() : 0);
		return result;
	}
}
