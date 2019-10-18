package com.tmobile.pacbot.azure.inventory;

import java.util.Map;

import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;


@Configuration
@ComponentScan
public class AzureDiscoveryApplication {

	public static Map<String, Object> collect(String[] args) {
		ApplicationContext context = new AnnotationConfigApplicationContext(AzureDiscoveryApplication.class);
		AzureFetchOrchestrator orchestrator = context.getBean(AzureFetchOrchestrator.class);
		return orchestrator.orchestrate();
	}
}

