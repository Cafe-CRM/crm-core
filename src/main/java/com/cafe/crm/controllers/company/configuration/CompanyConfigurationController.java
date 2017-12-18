package com.cafe.crm.controllers.company.configuration;

import com.cafe.crm.exceptions.company.configuration.StepByStepConfigurationException;
import com.cafe.crm.services.interfaces.company.configuration.ConfigurationStep;
import com.cafe.crm.services.interfaces.company.configuration.StepByStepConfiguration;
import com.cafe.crm.utils.SecurityUtils;
import com.google.common.io.ByteStreams;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.io.IOException;
import java.io.InputStream;

@Controller
public class CompanyConfigurationController {

	private final StepByStepConfiguration stepByStepConfiguration;
	private final ResourceLoader resourceLoader;

	@Autowired
	public CompanyConfigurationController(StepByStepConfiguration stepByStepConfiguration, ResourceLoader resourceLoader) {
		this.stepByStepConfiguration = stepByStepConfiguration;
		this.resourceLoader = resourceLoader;
	}

	@RequestMapping(value = "/boss/company/configuration", method = RequestMethod.GET)
	public String showStartSettings(Model model) throws IOException {
		ConfigurationStep step = stepByStepConfiguration.getNextStep();
		model.addAttribute("stepName", step.getStepName());

		return "company/configuration/configuration";
	}

	@RequestMapping(value = "/company/configuration/attention", method = RequestMethod.GET)
	public String showAttentionPage() {
		return "company/configuration/attention";
	}

	@RequestMapping(value = "/company/configuration/instruction/vk", method = RequestMethod.GET)
	public String showVkInstructionPage() {
		return "company/configuration/instruction/vk";
	}

	@RequestMapping(value = "/company/configuration/instruction/image/chat-id", produces = MediaType.IMAGE_JPEG_VALUE)

	public ResponseEntity<byte[]> getChatIdImage() throws IOException {
		Resource resourceImage = resourceLoader.getResource("classpath:doc-files/chat-id.png");
		InputStream imageInStream = resourceImage.getInputStream();
		byte[] imageContent = ByteStreams.toByteArray(imageInStream);
		final HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.IMAGE_JPEG);

		return new ResponseEntity<>(imageContent, headers, HttpStatus.OK);
	}

	@ExceptionHandler(value = StepByStepConfigurationException.class)
	public String handleStepByStepExceptionException(Authentication authentication) {
		String redirectPath = SecurityUtils.getStartPath(authentication);
		return "redirect:" + redirectPath;
	}
}
