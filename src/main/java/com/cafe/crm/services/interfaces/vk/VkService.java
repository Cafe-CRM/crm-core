package com.cafe.crm.services.interfaces.vk;


import com.cafe.crm.models.shift.Shift;
import com.cafe.crm.utils.Target;

public interface VkService {
	String sendDailyReportToConference(Shift shift);

	String getReportMessage(Shift shift);

	void sendConfirmToken(String prefix, Target target);
}
