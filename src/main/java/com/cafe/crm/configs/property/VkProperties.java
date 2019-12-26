package com.cafe.crm.configs.property;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties("vk")
public class VkProperties {

	private String applicationId;
	private String messageName;
	private String serviceChatId;
	private String adminChatId;
	private String accessToken;
	private String accessKey;
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

	public String getServiceChatId() {
		return serviceChatId;
	}

	public void setServiceChatId(String serviceChatId) {
		this.serviceChatId = serviceChatId;
	}

	public String getAdminChatId() {
		return adminChatId;
	}

	public void setAdminChatId(String adminChatId) {
		this.adminChatId = adminChatId;
	}

	public String getAccessToken() {
		return accessToken;
	}

	public String getAccessKey() {
		return accessKey;
	}

	public void setAccessKey(String accessKey) {
		this.accessKey = accessKey;
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
		dst.setServiceChatId(src.getServiceChatId());
		dst.setAdminChatId(src.getAdminChatId());
		dst.setMessageName(src.getMessageName());
		dst.setAccessKey(src.getAccessKey());
	}
}
