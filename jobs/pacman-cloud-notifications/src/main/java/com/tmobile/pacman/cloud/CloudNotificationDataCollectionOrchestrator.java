package com.tmobile.pacman.cloud;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.google.gson.Gson;
import com.tmobile.pacman.cloud.config.ConfigManager;
import com.tmobile.pacman.cloud.dao.RDSDBManager;
import com.tmobile.pacman.cloud.es.ElasticSearchRepository;
import com.tmobile.pacman.cloud.exception.DataException;
import com.tmobile.pacman.cloud.util.Constants;
import com.tmobile.pacman.cloud.util.Util;

@Component
public class CloudNotificationDataCollectionOrchestrator {

	/** The log. */
	private static final Logger LOGGER = LoggerFactory.getLogger(CloudNotificationDataCollectionOrchestrator.class);

	/** Clound Notification Query */
	private String cloudTargetQuery = "select * FROM CloudNotification_mapping"; // { "EC2" }; , "DIRECTCONNECT", "RDS",
																					// "LAMBDA", "IAM", "VPN",
																					// "CLOUDFRONT", "S3", "REDSHIFT", "SQS",
																					// "DYNAMODB", "ELASTICCACHE", "APIGATEWAY", 
																					//"VPC", "KMS", "MQ", "CONFIG", "CLOUDTRAIL" };

	private static final String CURR_DATE = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ").format(new java.util.Date());

	/** The es type. */
	private static String ESTYPE = "cloud_notification";

	/**
	 * Orchestrate.
	 * 
	 */
	public void orchestrate() {
		ExecutorService executor = Executors.newCachedThreadPool();
		executor.execute(() -> {
			try {
				dataCollection();
			} catch (Exception e) {
				LOGGER.error("Exception in startDataCollection " + Util.getStackTrace(e));
			}
		});
		executor.shutdown();
		while (!executor.isTerminated()) {
		}
	}

	/**
	 * Instantiates a new datacollection orchestrator.
	 *
	 * dataCollection method will iterate the targettypes and stores the
	 * notifications.
	 * 
	 */
	public void dataCollection() {

		try {
			List<Map<String, Object>> outputList = new ArrayList<Map<String, Object>>();
			List<Map<String, Object>> countList = new ArrayList<Map<String, Object>>();
			List<Map<String, String>> cloudMappings = RDSDBManager.executeQuery(cloudTargetQuery);
			List<Map<String, Object>> cloudNotificationObjs = new ArrayList<>();

			cloudMappings.parallelStream().forEach(cloudMapping -> {

				LOGGER.info("Started Collection for this Target Type**" + cloudMapping.get(Constants.EVENTTYPE));
				List<Map<String, Object>> phdEvents = new ArrayList<Map<String, Object>>();
				try {
					phdEvents = ElasticSearchRepository.getPhdEvents(cloudMapping.get(Constants.EVENTTYPE));
				} catch (DataException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				phdEvents.forEach(event -> {
					if (event.get(Constants.EVENTARN) != "") {
						try {
							String phdEntity = ElasticSearchRepository
									.getPhdEnityByArn(event.get(Constants.EVENTARN).toString());
							// System.out.println("**Entty**"+phdEntity);
							// Get the resources from pacbot
							if (phdEntity != null && !"UNKNOWN".equals(phdEntity) && !"AWS_ACCOUNT".equals(phdEntity)) {

								List<Map<String, Object>> resorceDet = ElasticSearchRepository.getPacResourceDet(
										cloudMapping.get(Constants.ESINDEX), cloudMapping.get(Constants.RESOURCEIDKEY),
										cloudMapping.get(Constants.RESOURCEIDVAL), phdEntity);
								if (!resorceDet.isEmpty()) {
									resorceDet.forEach(details -> {
										LOGGER.info("**Target type**" + cloudMapping.get(Constants.RESOURCEIDKEY));
										LOGGER.info(
												"***pac list**" + details.get(cloudMapping.get(Constants.RESOURCEIDVAL))
														+ "**DOCID***" + details.get(Constants._DOCID));
										countList.add(details);

										Map<String, Object> notificationObj = new HashMap<>();
										notificationObj.putAll(event);
										notificationObj.put("_docid", details.get("_docid"));
										notificationObj.put("_resourceid",
												details.get(cloudMapping.get(Constants.RESOURCEIDVAL)));
										notificationObj.put("latest", true);
										notificationObj.put("notificationId",
												details.get(cloudMapping.get(Constants.RESOURCEIDVAL)));
										notificationObj.put("type",
												cloudMapping.get(Constants.ESINDEX).toLowerCase().toString());
										cloudNotificationObjs.add(notificationObj);
									});
								} else {
									Map<String, Object> sourceMap = event;
									sourceMap.put("@id", event.get(Constants.ACCOUNTID).toString() + ":"
											+ event.get(Constants.EVENTARN).toString());
									outputList.add(sourceMap);
								}

							} else {
								Map<String, Object> sourceMap = event;
								sourceMap.put("@id", event.get(Constants.ACCOUNTID).toString() + ":"
										+ event.get(Constants.EVENTARN).toString());
								outputList.add(sourceMap);
							}
						} catch (DataException e) {
							LOGGER.error("Error in the cloudNotification" + e.getMessage());
						}
					}
				});
			});
			if (!cloudNotificationObjs.isEmpty()) {
				//cloudNotificationObjs.forEach(System.err::println);
				ElasticSearchRepository.uploadDataWithParent(cloudNotificationObjs);
			}
			// Storing the data
			if (!outputList.isEmpty()) {
				//outputList.forEach(System.err::println);
				LOGGER.info("UPLOADING SECURITYHUB DATA TO ES");

				ElasticSearchRepository.uploadData("cloud_notifications", ESTYPE, outputList, "@id", false);
			}
			LOGGER.info("**size**" + countList.size());
		} catch (Exception e) {
			LOGGER.error(" FAILED IN SECURITYHUB DATACOLLECTION JOB", Util.getStackTrace(e));
		}
	}
}
