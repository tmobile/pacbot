package com.tmobile.pacman.api.admin;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

import com.tmobile.pacman.api.admin.repository.service.ExceptionInitializerService;

@Component
public class EsIndexInitializer implements ApplicationListener<ContextRefreshedEvent> {

	@Autowired
	private ExceptionInitializerService exceptionInitializerService;
	
	@Override
	public void onApplicationEvent(final ContextRefreshedEvent event) {
		exceptionInitializerService.executeExceptionIndexInitializer();
	}
}