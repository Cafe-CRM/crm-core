package com.cafe.crm.services.interfaces.vk;


import com.cafe.crm.models.shift.Shift;

public interface VkService {
	void sendDailyReportToConference(Shift shift);
	public void sendConfirmToken();
}
