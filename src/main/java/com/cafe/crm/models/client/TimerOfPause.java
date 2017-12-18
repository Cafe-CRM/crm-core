package com.cafe.crm.models.client;

import com.cafe.crm.models.BaseEntity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.time.LocalDateTime;

@Entity
public class TimerOfPause extends BaseEntity {

	@Id
	@GeneratedValue
	private Long id;

	private Long idOfClient;

	private LocalDateTime startTime;

	private LocalDateTime endTime;

	private Long wholeTimePause = 0L;


	public TimerOfPause() {

	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getIdOfClient() {
		return idOfClient;
	}

	public void setIdOfClient(Long idOfClient) {
		this.idOfClient = idOfClient;
	}

	public LocalDateTime getStartTime() {
		return startTime;
	}

	public void setStartTime(LocalDateTime startTime) {
		this.startTime = startTime;
	}

	public LocalDateTime getEndTime() {
		return endTime;
	}

	public void setEndTime(LocalDateTime endTime) {
		this.endTime = endTime;
	}

	public Long getWholeTimePause() {
		return wholeTimePause;
	}

	public void setWholeTimePause(Long wholeTimePause) {
		this.wholeTimePause = wholeTimePause;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		TimerOfPause that = (TimerOfPause) o;

		if (id != null ? !id.equals(that.id) : that.id != null) return false;
		if (startTime != null ? !startTime.equals(that.startTime) : that.startTime != null) return false;
		if (endTime != null ? !endTime.equals(that.endTime) : that.endTime != null) return false;
		return wholeTimePause != null ? wholeTimePause.equals(that.wholeTimePause) : that.wholeTimePause == null;
	}

	@Override
	public int hashCode() {
		int result = id != null ? id.hashCode() : 0;
		result = 31 * result + (startTime != null ? startTime.hashCode() : 0);
		result = 31 * result + (endTime != null ? endTime.hashCode() : 0);
		result = 31 * result + (wholeTimePause != null ? wholeTimePause.hashCode() : 0);
		return result;
	}

	@Override
	public String toString() {
		return "TimerOfPause{" +
				"id=" + id +
				", startTime=" + startTime +
				", endTime=" + endTime +
				", wholeTimePause=" + wholeTimePause +
				'}';
	}
}
