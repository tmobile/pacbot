package com.tmobile.pacman.api.admin.repository.service;

import static com.tmobile.pacman.api.admin.common.AdminConstants.ES_EXCEPTION_INDEX;
import static com.tmobile.pacman.api.admin.common.AdminConstants.INIT_ES_CREATE_INDEX;

import java.util.Optional;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.tmobile.pacman.api.admin.repository.TaskRepository;
import com.tmobile.pacman.api.admin.repository.model.Task;
import com.tmobile.pacman.api.commons.utils.PacHttpUtils;

@Service
public class ExceptionInitializerServiceImpl implements ExceptionInitializerService
{
	private static final Logger LOG = LoggerFactory.getLogger(ExceptionInitializerServiceImpl.class);

	@Value("${elastic-search.host}")
	private String esHost;

	@Value("${elastic-search.port}")
	private int esPort;
	
	@Autowired
	private TaskRepository taskRepository;

	private static final String PROTOCOL = "http";

	private String esUrl;

	@PostConstruct
	void init()
	{
		esUrl = PROTOCOL + "://" + esHost + ":" + esPort;
	}

	@Override
	public void executeExceptionIndexInitializer()
	{
		LOG.info("triggering executeExceptionIndexInitializer");
		String exceptionIndex = esUrl.concat(ES_EXCEPTION_INDEX);
		try {
			int status = PacHttpUtils.getHttpHead(exceptionIndex);
			if (status == 404) {
				LOG.info("exception index cannot be found, intializing and creating exception index");
				Optional<Task> type = taskRepository.findByType(INIT_ES_CREATE_INDEX);
				if(type.isPresent()) {
					String mappings = type.get().getDetails();	
					PacHttpUtils.doHttpPut(exceptionIndex, mappings);
				}	
			}
		} catch (Exception exception) {
			LOG.info("Exception in executeExceptionIndexInitializer: {}", exception.getMessage());
			exception.printStackTrace();
		}
	}
}
