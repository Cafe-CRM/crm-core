package com.cafe.crm.configs.property;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties("vk")
public class VkProperties {

	private String applicationId;
	private String messageName;
	private String chatId;
	private String accessToken;
	private String apiVersion;

	public String getMessageName() {
		return messageName;
	}

	public void setMessageName(String messageName) {
		this.messageName = messageName;
	}

	public String getApplicationId() {
		return applicationId;
	}

	public void setApplicationId(String applicationId) {
		this.applicationId = applicationId;
	}

	public String getChatId() {
		return chatId;
	}

	public void setChatId(String chatId) {
		this.chatId = chatId;
	}

	public String getAccessToken() {
		return accessToken;
	}

	public void setAccessToken(String accessToken) {
		this.accessToken = accessToken;
	}

	public String getApiVersion() {
		return apiVersion;
	}

	public void setApiVersion(String apiVersion) {
		this.apiVersion = apiVersion;
	}

	public static void copy(VkProperties src, VkProperties dst) {
		dst.setAccessToken(src.getAccessToken());
		dst.setApiVersion(src.getApiVersion());
		dst.setApplicationId(src.getApplicationId());
		dst.setChatId(src.getChatId());
		dst.setMessageName(src.getMessageName());
	}
}
