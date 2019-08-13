package com.tmobile.pacman.api.asset.repository;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;
import org.springframework.util.CollectionUtils;

import com.google.common.base.Strings;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.tmobile.pacman.api.asset.AssetConstants;
import com.tmobile.pacman.api.commons.Constants;
import com.tmobile.pacman.api.commons.exception.DataException;
import com.tmobile.pacman.api.commons.repo.ElasticSearchRepository;
import com.tmobile.pacman.api.commons.repo.PacmanRdsRepository;
import com.tmobile.pacman.api.commons.utils.CommonUtils;
import com.tmobile.pacman.api.commons.utils.PacHttpUtils;

/**
 * Implemented class for CloudNotificationsRepository and all its method
 */
@Repository
public class CloudNotificationsRepositoryImpl implements CloudNotificationsRepository {

	@Autowired
	ElasticSearchRepository esRepository;

	@Autowired
	PacmanRdsRepository rdsRepository;

	private static final Log LOGGER = LogFactory.getLog(CloudNotificationsRepositoryImpl.class);
	private static final String _SEARCH = "_search";
	private static final String HITS = "hits";
	private static final String ERROR_IN_US = "error retrieving inventory from ES";
	@Value("${elastic-search.host}")
	private String esHost;
	@Value("${elastic-search.port}")
	private int esPort;
	private String TYPE = "cloud_notification";
	private String INDEX = "cloud_notifications";
	private String AUTOFIXTYPE = "autofixplan";
	final static String protocol = "http";
	private String esUrl;
	private static final String _SOURCE = "_source";
	private static final String _COUNT = "_count";
	List<Map<String, Object>> notifications = new ArrayList<>();
	String autoFixQuery = "";

	@PostConstruct
	void init() {
		esUrl = protocol + "://" + esHost + ":" + esPort;
	}

	@Override
	public List<Map<String, Object>> getNotifications(String assetGroup, Map<String, String> filter,
			boolean globalNotifier, int size, int from) {
		LOGGER.info("Inside getNotifications");
		notifications = new ArrayList<>();
		try {
			
			if (globalNotifier) {
				getCloudNotifications(INDEX, TYPE, filter, size, from).forEach(notification -> {
					notifications.add(notification);
				});
			} else {
				
				String eventCat = filter.get(Constants.EVENTCATEGORY);
				String resourceId = filter.get(Constants.RESOURCEID);
				if ((filter.isEmpty() || filter.containsValue("Autofix") || Strings.isNullOrEmpty(eventCat)) && (Strings.isNullOrEmpty(resourceId)) ) {
					getAutofixProjections(assetGroup, AUTOFIXTYPE, filter).forEach(autofix -> {
						notifications.add(autofix);
					});
				}
				
				getCloudNotifications(assetGroup, TYPE, filter, size, from).forEach(notification -> {
					notifications.add(notification);
				});
			}
		} catch (Exception e) {
			LOGGER.error("Error in getNotifications", e);
		}
		Comparator<Map<String, Object>> comp = (m1, m2) -> LocalDate
                .parse(m2.get("startTime").toString().substring(0, 10), DateTimeFormatter.ISO_DATE)
                .compareTo(LocalDate.parse(m1.get("startTime").toString().substring(0, 10), DateTimeFormatter.ISO_DATE));
        Collections.sort(notifications, comp);

		LOGGER.info("Exiting getNotifications");
		return notifications.stream().distinct().collect(Collectors.toList());
	}

	
	@Override
	public List<Map<String, Object>> getCloudNotificationDetail(String eventArn, boolean globalNotifier,
			String assetGroup) {
		LOGGER.info("Inside getCloudNotificationDetail");
		List<Map<String, Object>> detail = new ArrayList<>();
		try {
			if (globalNotifier) {
				detail = getCloudNotificationDetail(INDEX, TYPE, eventArn);
			} else {
				detail = getCloudNotificationDetail(assetGroup, TYPE, eventArn);
			}

		} catch (Exception e) {
			LOGGER.error("Error in getCloudNotificationDetail", e);
		}
		LOGGER.info("Exiting getCloudNotificationDetail");
		return detail;
	}
	
	@SuppressWarnings({ "deprecation" })
	private List<Map<String, Object>> getAssetsByResourceId(String assetGroupName, String type, String resourceId) {

		List<Map<String, Object>> results = new ArrayList<>();
		String query = "SELECT esIndex, resourceIdVal FROM CloudNotification_mapping WHERE esIndex =\"" + type + "\"";
		try {
			results = rdsRepository.getDataFromPacman(query);
		} catch (Exception exception) {
			LOGGER.error("Error in getAssetsByResourceId for getting parent type ", exception);
		}
		Map<String, Object> mustFilter = new HashMap<>();
		mustFilter.put(CommonUtils.convertAttributetoKeyword((results.get(0).get("resourceIdVal")).toString()),
				resourceId);
		mustFilter.put(Constants.LATEST, Constants.TRUE);
		mustFilter.put(AssetConstants.UNDERSCORE_ENTITY, Constants.TRUE);
		Map<String, Object> mustNotFilter = null;

		List<Map<String, Object>> assets = new ArrayList<>();
		try {
			if (AssetConstants.ALL.equals(type)) {
				try {
					Map<String, Object> mustTermsFilter = new HashMap<>();
					assets = esRepository.getDataFromES(assetGroupName, null, mustFilter, null, null, null,
							mustTermsFilter);
				} catch (Exception e) {
					LOGGER.error(AssetConstants.ERROR_GETASSETSBYAG, e);
				}
			} else {
				assets = esRepository.getDataFromES(assetGroupName, (results.get(0).get("esIndex")).toString(),
						mustFilter, mustNotFilter, null, null, null);
			}
		} catch (Exception e) {
			LOGGER.error(AssetConstants.ERROR_GETASSETSBYAG, e);
		}
		return assets;
	}
	
	@SuppressWarnings("unchecked")
	public List<Map<String, Object>> getCloudNotificationDetail(String index, String type, String eventArn)
			throws DataException {
		Gson gson = new GsonBuilder().create();
		String responseDetails = null;
		StringBuilder requestBody = null;
		List<Map<String, Object>> cloudDetails = null;
		List<String> fieldNames = new ArrayList<>();
		List<Map<String, Object>> detail = new ArrayList<Map<String, Object>>();
		StringBuilder urlToQueryBuffer = new StringBuilder(esUrl).append("/").append(index).append("/").append(type)
				.append("/").append(_SEARCH);
		String body = "{\"_source\": [\"eventarn\",\"notificationId\",\"latestdescription\", \"type\"], \"query\":{\"bool\":{\"must\":[{\"term\":{\"eventarn.keyword\":\""
				+ eventArn + "\"}},{\"term\":{\"latest\":\"true\"}}]}}}";
		requestBody = new StringBuilder(body);
		try {
			responseDetails = PacHttpUtils.doHttpPost(urlToQueryBuffer.toString(), requestBody.toString());
		} catch (Exception e) {
			LOGGER.error(ERROR_IN_US, e);
			throw new DataException(e);
		}
		Map<String, Object> responseMap = (Map<String, Object>) gson.fromJson(responseDetails, Object.class);
		if (responseMap.containsKey(HITS)) {
			Map<String, Object> hits = (Map<String, Object>) responseMap.get(HITS);
			if (hits.containsKey(HITS)) {
				cloudDetails = (List<Map<String, Object>>) hits.get(HITS);
				List<String> resources = new ArrayList<String>();
				String resourceType = "";
				for (Map<String, Object> cloudDetail : cloudDetails) {
					Map<String, Object> sourceMap = (Map<String, Object>) cloudDetail.get("_source");
					if (sourceMap.containsKey("notificationId")) {
						resources.add(sourceMap.get("notificationId").toString());
						resourceType = sourceMap.get("type").toString();
						detail.add(getAssetsByResourceId(index, resourceType, resources.get(0).toString()).get(0));
					}
				}
			}
		}
		List<String> fieldsToBeSkipped = Arrays.asList(Constants.RESOURCEID, Constants.DOCID,
				AssetConstants.UNDERSCORE_ENTITY, Constants._ID, AssetConstants.UNDERSCORE_LOADDATE,
				Constants.ES_DOC_PARENT_KEY, Constants.ES_DOC_ROUTING_KEY, AssetConstants.CREATE_TIME,
				AssetConstants.FIRST_DISCOVEREDON, AssetConstants.DISCOVERY_DATE, Constants.LATEST,
				AssetConstants.CREATION_DATE);
		return formGetListResponse(fieldNames, detail, fieldsToBeSkipped);
	}

	private List<Map<String, Object>> formGetListResponse(List<String> fieldNames,
			List<Map<String, Object>> assetDetails, List<String> fieldsToBeSkipped) {

		List<Map<String, Object>> assetList = new ArrayList<>();
		if (!CollectionUtils.isEmpty(fieldNames)) {
			final List<String> fieldNamesCopy = fieldNames;
			assetDetails.parallelStream().forEach(assetDetail -> {
				Map<String, Object> asset = new LinkedHashMap<>();
				for (String fieldName : fieldNamesCopy) {
					if (!assetDetail.containsKey(fieldName)) {
						asset.put(fieldName, "");
					} else {
						asset.put(fieldName, assetDetail.get(fieldName));
					}
				}
				synchronized (assetList) {
					assetList.add(asset);
				}
			});
			return assetList;
		} else {
			assetDetails.parallelStream().forEach(assetDetail -> {
				Map<String, Object> asset = new LinkedHashMap<>();
				asset.put(Constants.RESOURCEID, assetDetail.get(Constants.RESOURCEID));
				assetDetail.forEach((key, value) -> {
					if (!fieldsToBeSkipped.contains(key)) {
						asset.put(key, value);
					}
				});
				synchronized (assetList) {
					assetList.add(asset);
				}
			});
			return assetList;
		}
	}

	@Override
	@SuppressWarnings("unchecked")
	public Map<String, Object> getCloudNotificationInfo(String eventArn, boolean globalNotifier, String assetGroup) {
		Map<String, Object> eventMap = new HashMap<>();
		eventMap.put("scheduledChange", "Scheduled Change");
		eventMap.put("accountNotification", "Account Notification");
		eventMap.put("issue", "Issue");

		String index = "";
		String type = "";
		if (globalNotifier) {
			index = INDEX;
			type = TYPE;
		} else {
			index = assetGroup;
			type = TYPE;
		}
		Gson gson = new GsonBuilder().create();
		String responseDetails = null;
		StringBuilder requestBody = null;
		List<Map<String, Object>> cloudDetails = null;
		StringBuilder urlToQueryBuffer = new StringBuilder(esUrl).append("/").append(index).append("/").append(type)
				.append("/").append(_SEARCH);

		String body = "{\"_source\": [\"eventarn\",\"endtime\",\"eventtypecategory\",\"starttime\",\"statuscode\",\"eventtypecode\",\"eventregion\",\"latestdescription\", \"type\"], \"query\":{\"bool\":{\"must\":[{\"term\":{\"eventarn.keyword\":\""
				+ eventArn + "\"}},{\"term\":{\"latest\":\"true\"}}]}}}";
		requestBody = new StringBuilder(body);
		try {
			responseDetails = PacHttpUtils.doHttpPost(urlToQueryBuffer.toString(), requestBody.toString());
		} catch (Exception e) {
			LOGGER.error(ERROR_IN_US, e);
			try {
				throw new DataException(e);
			} catch (DataException e1) {
				LOGGER.error("ERROR in getCloudNotificationInfo ", e1);
			}
		}
		Map<String, Object> responseMap = (Map<String, Object>) gson.fromJson(responseDetails, Object.class);
		Map<String, Object> infoMap = new HashMap<String, Object>();
		if (responseMap.containsKey(HITS)) {
			Map<String, Object> hits = (Map<String, Object>) responseMap.get(HITS);
			if (hits.containsKey(HITS)) {
				cloudDetails = (List<Map<String, Object>>) hits.get(HITS);
				for (Map<String, Object> cloudDetail : cloudDetails) {
					Map<String, Object> sourceMap = (Map<String, Object>) cloudDetail.get("_source");
					infoMap.put("event", CommonUtils.capitailizeWord(sourceMap.get("eventtypecode").toString()));
					infoMap.put("status", sourceMap.get("statuscode"));
					infoMap.put("region", sourceMap.get("eventregion"));
					infoMap.put("startTime", sourceMap.get("starttime"));
					infoMap.put("endTime", sourceMap.get("endtime"));
					infoMap.put("eventCategory", eventMap.get(sourceMap.get("eventtypecategory")));
					infoMap.put("eventarn", sourceMap.get("eventarn"));
					infoMap.put("latestdescription", eventDescChanges(sourceMap.get("latestdescription").toString()));
				}
			}
		}
		return infoMap;
	}

	private String eventDescChanges(String description) {

		description = description.replace("*", "<h6>*");
		description = description.replace("?[NL]", "?</h6>");
		if (description.indexOf("https://console.aws.amazon.com/ec2/v2/home?region=us-west-2#Events") > 0) {
			description = description.replace("https://console.aws.amazon.com/ec2/v2/home?region=us-west-2#Events",
					"<a href =\"https://console.aws.amazon.com/ec2/v2/home?region=us-west-2#Events\" target=\"_blank\">https://console.aws.amazon.com/ec2/v2/home?region=us-west-2#Events</a>");
		}
		if (description.indexOf(
				"https://docs.aws.amazon.com/AWSEC2/latest/UserGuide/monitoring-instances-status-check_sched.html#schedevents_actions_reboot") > 0) {
			description = description.replace(
					"https://docs.aws.amazon.com/AWSEC2/latest/UserGuide/monitoring-instances-status-check_sched.html#schedevents_actions_reboot",
					"<a href =\"https://docs.aws.amazon.com/AWSEC2/latest/UserGuide/monitoring-instances-status-check_sched.html#schedevents_actions_reboot\" target=\"_blank\">https://docs.aws.amazon.com/AWSEC2/latest/UserGuide/monitoring-instances-status-check_sched.html#schedevents_actions_reboot</a>");
		}
		if (description.indexOf(
				"at https://docs.aws.amazon.com/AWSEC2/latest/UserGuide/monitoring-instances-status-check_sched.html") > 0) {
			description = description.replace(
					"at https://docs.aws.amazon.com/AWSEC2/latest/UserGuide/monitoring-instances-status-check_sched.html",
					"<a href =\"https://docs.aws.amazon.com/AWSEC2/latest/UserGuide/monitoring-instances-status-check_sched.html\" target=\"_blank\">https://docs.aws.amazon.com/AWSEC2/latest/UserGuide/monitoring-instances-status-check_sched.html</a>");
		}
		if (description.indexOf("https://docs.aws.amazon.com/AWSEC2/latest/UserGuide/Stop_Start.html") > 0) {
			description = description.replace("https://docs.aws.amazon.com/AWSEC2/latest/UserGuide/Stop_Start.html",
					"<a href =\"https://docs.aws.amazon.com/AWSEC2/latest/UserGuide/Stop_Start.html\" target=\"_blank\">https://docs.aws.amazon.com/AWSEC2/latest/UserGuide/Stop_Start.html</a>");
		}
		if (description.indexOf("https://console.aws.amazon.com/ec2/v2/home?region=us-east-1#Events") > 0) {
			description = description.replace("https://console.aws.amazon.com/ec2/v2/home?region=us-east-1#Events",
					"<a href =\"https://console.aws.amazon.com/ec2/v2/home?region=us-east-1#Events\" target=\"_blank\">https://console.aws.amazon.com/ec2/v2/home?region=us-east-1#Events</a>");
		}
		if (description.indexOf("[3] https://github.com/awslabs/aws-vpn-migration-scripts") > 0) {
			description = description.replace("[3] https://github.com/awslabs/aws-vpn-migration-scripts",
					"<a href =\"https://github.com/awslabs/aws-vpn-migration-scripts\" target=\"_blank\">https://github.com/awslabs/aws-vpn-migration-scripts</a>");
		}
		if (description.indexOf("http://docs.aws.amazon.com/AWSEC2/latest/UserGuide/instance-retirement.html") > 0) {
			description = description.replace(
					" http://docs.aws.amazon.com/AWSEC2/latest/UserGuide/instance-retirement.html",
					"<a href =\" http://docs.aws.amazon.com/AWSEC2/latest/UserGuide/instance-retirement.html\" target=\"_blank\">http://docs.aws.amazon.com/AWSEC2/latest/UserGuide/instance-retirement.html</a>");
		}
		if (description.indexOf("(https://aws.amazon.com/support)") > 0) {
			description = description.replace("(https://aws.amazon.com/support)",
					"<a href =\"https://aws.amazon.com/support\" target=\"_blank\">https://aws.amazon.com/support</a>");
		}
		if (description.indexOf("(http://aws.amazon.com/support)") > 0) {
			description = description.replace("(http://aws.amazon.com/support)",
					"<a href =\"http://aws.amazon.com/support\" target=\"_blank\">http://aws.amazon.com/support</a>");
		}
		if (description.indexOf("http://aws.amazon.com/support") > 0) {
			description = description.replace("http://aws.amazon.com/support",
					"<a href =\"http://aws.amazon.com/support\" target=\"_blank\">http://aws.amazon.com/support</a>");
		}
		if (description.indexOf("[1] https://console.aws.amazon.com") > 0) {
			description = description.replace("[1] https://console.aws.amazon.com",
					"<a href =\"https://console.aws.amazon.com\" target=\"_blank\">https://console.aws.amazon.com</a>");
		}
		if (description.indexOf("http://aws.amazon.com/architecture") > 0) {
			description = description.replace("http://aws.amazon.com/architecture",
					"<a href =\"http://aws.amazon.com/architecture\" target=\"_blank\">http://aws.amazon.com/architecture</a>");
		}
		if (description.indexOf("https://aws.amazon.com/support") > 0) {
			description = description.replace("https://aws.amazon.com/support",
					"<a href =\"https://aws.amazon.com/support\" target=\"_blank\">https://aws.amazon.com/support</a>");
		}
		if (description.indexOf("https://aws.amazon.com/premiumsupport/knowledge-center/migrate-classic-vpn-new") > 0) {
			description = description.replace(
					"https://aws.amazon.com/premiumsupport/knowledge-center/migrate-classic-vpn-new",
					"<a href =\"https://aws.amazon.com/premiumsupport/knowledge-center/migrate-classic-vpn-new\" target=\"_blank\">https://aws.amazon.com/premiumsupport/knowledge-center/migrate-classic-vpn-new</a>");
		}
		if (description.indexOf(
				"https://docs.aws.amazon.com/amazon-mq/latest/developer-guide/using-amazon-mq-securely.html#amazon-mq-vpc-security-groups") > 0) {
			description = description.replace(
					"https://docs.aws.amazon.com/amazon-mq/latest/developer-guide/using-amazon-mq-securely.html#amazon-mq-vpc-security-groups",
					"<a href =\"https://docs.aws.amazon.com/amazon-mq/latest/developer-guide/using-amazon-mq-securely.html#amazon-mq-vpc-security-groups\" target=\"_blank\">https://docs.aws.amazon.com/amazon-mq/latest/developer-guide/using-amazon-mq-securely.html#amazon-mq-vpc-security-groups</a>");
		}
		if (description.indexOf(
				"[3] http://activemq.apache.org/security-advisories.data/CVE-2019-0222-announcement.txt") > 0) {
			description = description.replace(
					"[3] http://activemq.apache.org/security-advisories.data/CVE-2019-0222-announcement.txt",
					"<a href =\"http://activemq.apache.org/security-advisories.data/CVE-2019-0222-announcement.txt\" target=\"_blank\">http://activemq.apache.org/security-advisories.data/CVE-2019-0222-announcement.txt</a>");
		}
		if (description.indexOf(
				"https://docs.aws.amazon.com/amazon-mq/latest/developer-guide/amazon-mq-editing-broker-preferences.html") > 0) {
			description = description.replace(
					"https://docs.aws.amazon.com/amazon-mq/latest/developer-guide/amazon-mq-editing-broker-preferences.html",
					"<a href =\"https://docs.aws.amazon.com/amazon-mq/latest/developer-guide/amazon-mq-editing-broker-preferences.html\" target=\"_blank\">https://docs.aws.amazon.com/amazon-mq/latest/developer-guide/amazon-mq-editing-broker-preferences.html</a>");
		}
		if (description.indexOf("https://docs.aws.amazon.com/vpn/latest/s2svpn/VPC_VPN.html#vpn-categories") > 0) {
			description = description.replace(
					"https://docs.aws.amazon.com/vpn/latest/s2svpn/VPC_VPN.html#vpn-categories",
					"<a href =\"https://docs.aws.amazon.com/vpn/latest/s2svpn/VPC_VPN.html#vpn-categories\" target=\"_blank\">https://docs.aws.amazon.com/vpn/latest/s2svpn/VPC_VPN.html#vpn-categories</a>");
		}
		if (description.indexOf("https://nodejs.org/en/blog/release/v6.9.0/") > 0) {
			description = description.replace("https://nodejs.org/en/blog/release/v6.9.0/",
					"<a href =\"https://nodejs.org/en/blog/release/v6.9.0\" target=\"_blank\">https://nodejs.org/en/blog/release/v6.9.0/</a>");
		}
		if (description.indexOf("https://docs.aws.amazon.com/lambda/latest/dg/runtime-support-policy.html") > 0) {
			description = description.replace(
					"https://docs.aws.amazon.com/lambda/latest/dg/runtime-support-policy.html",
					"<a href =\"https://docs.aws.amazon.com/lambda/latest/dg/runtime-support-policy.html\" target=\"_blank\">https://docs.aws.amazon.com/lambda/latest/dg/runtime-support-policy.html</a>");
		}
		if (description.indexOf(
				"https://aws.amazon.com/blogs/compute/node-js-8-10-runtime-now-available-in-aws-lambda/") > 0) {
			description = description.replace(
					"https://aws.amazon.com/blogs/compute/node-js-8-10-runtime-now-available-in-aws-lambda/",
					"<a href =\"https://aws.amazon.com/blogs/compute/node-js-8-10-runtime-now-available-in-aws-lambda\" target=\"_blank\">https://aws.amazon.com/blogs/compute/node-js-8-10-runtime-now-available-in-aws-lambda</a>");
		}
		if (description.indexOf(
				"https://aws.amazon.com/blogs/compute/upcoming-updates-to-the-aws-lambda-execution-environment/") > 0) {
			description = description.replace(
					"https://aws.amazon.com/blogs/compute/upcoming-updates-to-the-aws-lambda-execution-environment/",
					"<a href =\"https://aws.amazon.com/blogs/compute/upcoming-updates-to-the-aws-lambda-execution-environment\" target=\"_blank\">https://aws.amazon.com/blogs/compute/upcoming-updates-to-the-aws-lambda-execution-environment</a>");
		}
		if (description.indexOf(
				"https://aws.amazon.com/blogs/compute/updated-timeframe-for-the-upcoming-aws-lambda-and-aws-lambdaedge-execution-environment-update/") > 0) {
			description = description.replace(
					"https://aws.amazon.com/blogs/compute/updated-timeframe-for-the-upcoming-aws-lambda-and-aws-lambdaedge-execution-environment-update/",
					"<a href =\"https://aws.amazon.com/blogs/compute/updated-timeframe-for-the-upcoming-aws-lambda-and-aws-lambdaedge-execution-environment-update\" target=\"_blank\">https://aws.amazon.com/blogs/compute/updated-timeframe-for-the-upcoming-aws-lambda-and-aws-lambdaedge-execution-environment-update</a>");
		}

		description = description.replace("[1]", "").replace("[2]", "").replace("[3]", "").replace("[4]", "")
				.replace("[NL][NL]", "\n\n").replace("(https:", "<a href=\"https:").replace("[NL]", "\n")
				.replace(" [3]", "");
		return description;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.tmobile.pacman.api.compliance.repository.
	 * CloudNotificationsRepositoryImpl# getGlobalNotifications(java.lang.String)
	 */
	@SuppressWarnings("unchecked")
	public List<Map<String, Object>> getCloudNotifications(String index, String type, Map<String, String> filter,
			int size, int from) throws DataException {
		Map<String, Object> eventMap = new HashMap<>();
		eventMap.put("scheduledChange", "Scheduled Change");
		eventMap.put("accountNotification", "Account Notification");
		eventMap.put("issue", "Issue");
		List<Map<String, Object>> notificationsList = new ArrayList<Map<String, Object>>();
		try {
			Map<String, Object> eventArnMap = new HashMap<String, Object>();
			eventArnMap = getCloudEventArn(index, type, size, from);
			String eventArn = eventArnMap.keySet().stream().collect(Collectors.toList()).stream()
					.collect(Collectors.joining("\",\"", "\"", "\""));

			String body = "";
			String evenCategory = filterkey(filter, Constants.EVENTCATEGORY);
			String resourceId = filterkey(filter, Constants.RESOURCEID);

			String eventStatus = filterkey(filter, Constants.EVENTSTATUS);
			if(evenCategory.contains("Autofix") && (Strings.isNullOrEmpty(resourceId))) {
				getAutofixProjections(index, AUTOFIXTYPE, filter).forEach(autofix -> {
					notificationsList.add(autofix);
				});
			}
			
			body = "{\"size\":10000,\"_source\":[\"eventarn\",\"eventtypecode\",\"statuscode\",\"eventregion\",\"starttime\",\"endtime\",\"eventtypecategory\"],\"query\":{\"bool\":{\"must\":[{\"terms\":{\"eventarn.keyword\":["
					+ eventArn + "]}},{\"term\":{\"latest\":\"true\"}}";
			if (!Strings.isNullOrEmpty(evenCategory)) {
				body = body + ",{\"terms\":{\"eventtypecategory.keyword\":" + evenCategory + "}}";
			}
			if (!Strings.isNullOrEmpty(resourceId)) {
				body = body + ",{\"terms\":{\"_resourceid.keyword\":" + resourceId + "}}";
			}
			if (!Strings.isNullOrEmpty(eventStatus)) {
				body = body + ",{\"terms\":{\"statuscode.keyword\":" + eventStatus + "}}";
			}
			body = body + "]}},\"sort\":[{\"starttime.keyword\":{\"order\":\"desc\"}}]}}";
			String urlToQuery = esRepository.buildESURL(esUrl, index, type, size, from);
			Gson gson = new GsonBuilder().create();
			String responseDetails = null;
			try {
				responseDetails = PacHttpUtils.doHttpPost(urlToQuery, new StringBuilder(body).toString());
			} catch (Exception e) {
				LOGGER.error("Error in getCloudNotifications", e);
			}
			Map<String, Object> response = (Map<String, Object>) gson.fromJson(responseDetails, Object.class);
			if (response.containsKey(HITS)) {
				Map<String, Object> hits = (Map<String, Object>) response.get(HITS);
				if (hits.containsKey(HITS)) {
					List<Map<String, Object>> hitDetails = (List<Map<String, Object>>) hits.get(HITS);
					if (!hitDetails.isEmpty()) {
						for (Map<String, Object> hitDetail : hitDetails) {
							Map<String, Object> sources = (Map<String, Object>) hitDetail.get(_SOURCE);
							Map<String, Object> notifcation = new LinkedHashMap<String, Object>();
							notifcation.put("event",
									CommonUtils.capitailizeWord(sources.get("eventtypecode").toString()));
							notifcation.put("status", sources.get("statuscode"));
							notifcation.put("region", sources.get("eventregion"));
							notifcation.put("startTime", sources.get("starttime"));
							notifcation.put("endTime", sources.get("endtime"));
							if (!index.equalsIgnoreCase("cloud_notifications")) {
								notifcation.put("affectedResources", eventArnMap.get(sources.get("eventarn")));
							}
							notifcation.put("eventCategory", eventMap.get(sources.get("eventtypecategory")));
							notifcation.put("eventarn", sources.get("eventarn"));
							notificationsList.add(notifcation);
						}
					}
				}
			}
		} catch (Exception e) {
			LOGGER.error("Error in getCloudNotifications", e);
		}
		return notificationsList.stream().distinct().collect(Collectors.toList());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.tmobile.pacman.api.compliance.repository.
	 * CloudNotificationsRepositoryImpl# getGlobalNotifications(java.lang.String)
	 */
	@SuppressWarnings("unchecked")
	public Map<String, Object> getCloudEventArn(String index, String type, int size, int from) throws DataException {

		Gson gson = new GsonBuilder().create();
		String responseDetails = null;
		StringBuilder requestBody = null;
		StringBuilder urlToQueryBuffer = new StringBuilder(esUrl).append("/").append(index).append("/").append(type)
				.append("/").append(_SEARCH);

		String body = "{\"query\":{\"bool\":{\"must\":[{\"match\":{\"latest\":true}}]}},\"aggs\":{\"name\":{\"terms\":{\"field\":\"eventarn.keyword\",\"size\":1000}}},"
				+ "\"sort\":[{\"starttime.keyword\":{\"order\":\"desc\"}}]}";
		requestBody = new StringBuilder(body);
		try {
			responseDetails = PacHttpUtils.doHttpPost(urlToQueryBuffer.toString(), requestBody.toString());
		} catch (Exception e) {
			LOGGER.error(ERROR_IN_US, e);
			throw new DataException(e);
		}
		Map<String, Object> response = (Map<String, Object>) gson.fromJson(responseDetails, Map.class);
		Map<String, Object> aggregations = (Map<String, Object>) response.get(Constants.AGGREGATIONS);
		Map<String, Object> name = (Map<String, Object>) aggregations.get("name");
		List<Map<String, Object>> buckets = (List<Map<String, Object>>) name.get(Constants.BUCKETS);

		return buckets.parallelStream().filter(buket -> buket.get("doc_count") != null)
				.collect(Collectors.toMap(buket -> buket.get("key").toString(), buket -> buket.get("doc_count"),
						(oldValue, newValue) -> newValue));
	}

	@SuppressWarnings("unchecked")
	private long getTotalDocCount(String index, String type, String requestBody) {
		StringBuilder urlToQuery = new StringBuilder(esUrl).append("/").append(index).append("/").append(type)
				.append("/").append(_SEARCH);
		String responseDetails = null;
		Gson gson = new GsonBuilder().create();
		try {
			responseDetails = PacHttpUtils.doHttpPost(urlToQuery.toString(), requestBody);
			Map<String, Object> response = (Map<String, Object>) gson.fromJson(responseDetails, Map.class);
			Map<String, Object> aggregations = (Map<String, Object>) response.get(Constants.AGGREGATIONS);
			Map<String, Object> name = (Map<String, Object>) aggregations.get("name");
			List<Map<String, Object>> buckets = (List<Map<String, Object>>) name.get(Constants.BUCKETS);
			return (long) (buckets.size());
		} catch (Exception e) {
			LOGGER.error("Error in getTotalDocCount", e);
			return 0;
		}
	}

	/**
	 * Gets the type count.
	 *
	 * @param indexName the index name
	 * @param type      the type
	 * @return the type count
	 */
	private int getAutoFixSummary(String indexName, String type) {

		StringBuilder urlToQuery = new StringBuilder(esUrl).append("/").append(indexName).append("/").append(type)
				.append("/").append(_COUNT).append("?filter_path=count");
		String requestBody = "{}";
		String responseDetails = null;
		try {
			responseDetails = PacHttpUtils.doHttpPost(urlToQuery.toString(), requestBody);
			JsonParser jsonParser = new JsonParser();
			JsonObject resultJson = (JsonObject) jsonParser.parse(responseDetails);
			return resultJson.get("count").getAsInt();
		} catch (Exception e) {
			LOGGER.error("Error in getTotalDocCount", e);
			return 0;
		}
	}
	
	@SuppressWarnings("unchecked")
	public List<Map<String, Object>> getAutofixProjections(String index, String type, Map<String, String> filter)
			throws DataException {
		List<Map<String, Object>> autofixPlanList = new ArrayList<Map<String, Object>>();
		try {
			StringBuilder urlToQuery = new StringBuilder(esUrl).append("/").append(index).append("/").append(type)
					.append("/").append(_SEARCH);
			String body = "";
			body = "{\"size\":10000,\"_source\":[\"docId\",\"planItems\",\"ruleId\",\"issueId\",\"resourceId\",\"resourceType\"]}";
			Gson gson = new GsonBuilder().create();
			String responseDetails = null;
			try {
				responseDetails = PacHttpUtils.doHttpPost(urlToQuery.toString(), new StringBuilder(body).toString());
			} catch (Exception e) {
				LOGGER.error("Error in getAutofixProjections", e);
			}
			Map<String, Object> response = (Map<String, Object>) gson.fromJson(responseDetails, Object.class);
			if (response.containsKey(HITS)) {
				Map<String, Object> hits = (Map<String, Object>) response.get(HITS);
				if (hits.containsKey(HITS)) {
					List<Map<String, Object>> hitDetails = (List<Map<String, Object>>) hits.get(HITS);
					if (!hitDetails.isEmpty()) {
						for (Map<String, Object> hitDetail : hitDetails) {
							Map<String, Object> sources = (Map<String, Object>) hitDetail.get(_SOURCE);
							Map<String, Object> notifcation = new LinkedHashMap<String, Object>();
							notifcation.put("event", "Aws "+ sources.get("resourceType")+ " Autofix");
							notifcation.put("eventCategory", "Autofix");
							notifcation.put("eventarn", sources.get("resourceId"));
							Object planStatus = sources.get("planStatus");
							List<Map<String, Object>> planitems = (List<Map<String, Object>>) sources.get("planItems");
							
							if(planitems!=null && !planitems.isEmpty()) {
								
								notifcation.put("startTime", planitems.get(0).get("plannedActionTime"));
								notifcation.put("endTime", planitems.get(planitems.size()-1).get("plannedActionTime"));
								
								if(planStatus==null) {
									planStatus =  planitems.get(planitems.size()-1).get("status");
								}
								
							}
							if(planStatus!=null) {
								notifcation.put("status",planStatus.toString().toLowerCase());
								
							}else {
								notifcation.put("status","unknown");
							}
							notifcation.put("affectedResources", 1);
							autofixPlanList.add(notifcation);
						}
					}
				}
			}
			Comparator<Map<String, Object>> comp = (m1, m2) -> LocalDate
					.parse(m2.get("endTime").toString().substring(0, 10), DateTimeFormatter.ISO_DATE)
					.compareTo(LocalDate.parse(m1.get("endTime").toString().substring(0, 10), DateTimeFormatter.ISO_DATE));
			Collections.sort(autofixPlanList, comp);
		} catch (Exception e) {
			LOGGER.error("Error in getAutofixProjections", e);
		}
		return autofixPlanList;
	}
	
	@SuppressWarnings("unchecked")
	public Map<String, Object> getAutofixProjectionDetail(String ag, Map<String, String> filter) {
		Map<String, Object> autofixPlanDet = new LinkedHashMap<String, Object>();
		autoFixQuery = "";
		try {
			StringBuilder urlToQuery = new StringBuilder(esUrl).append("/").append(ag).append("/").append(AUTOFIXTYPE)
					.append("/").append(_SEARCH);
			filter.entrySet().forEach(autofix->{
				autoFixQuery = "{\"size\":1,\"_source\":[\"docId\",\"planItems\",\"ruleId\",\"issueId\",\"resourceId\",\"resourceType\"],\"query\":{\"match\":{\""
						+autofix.getKey()+".keyword"+"\":\""
						+autofix.getValue()+"\"}}}";
			});
			Gson gson = new GsonBuilder().create();
			String responseDetails = null;
			try {
				responseDetails = PacHttpUtils.doHttpPost(urlToQuery.toString(), new StringBuilder(autoFixQuery).toString());
			} catch (Exception e) {
				LOGGER.error("Error in getAutofixProjectionDetail", e);
			}
			Map<String, Object> response = (Map<String, Object>) gson.fromJson(responseDetails, Object.class);
			if (response.containsKey(HITS)) {
				Map<String, Object> hits = (Map<String, Object>) response.get(HITS);
				if (hits.containsKey(HITS)) {
					List<Map<String, Object>> hitDetails = (List<Map<String, Object>>) hits.get(HITS);
					if (!hitDetails.isEmpty()) {
						for (Map<String, Object> hitDetail : hitDetails) {
							Map<String, Object> sources = (Map<String, Object>) hitDetail.get(_SOURCE);
							autofixPlanDet.put("event", "Aws "+ sources.get("resourceType")+ " Autofix");
							
							autofixPlanDet.put("eventCategory", "Autofix");
							autofixPlanDet.put("eventarn", sources.get("resourceId"));
							autofixPlanDet.put("planItems", sources.get("planItems"));
							autofixPlanDet.put("issueId", sources.get("issueId"));
							autofixPlanDet.put("resourceId", sources.get("resourceId"));
							autofixPlanDet.put("ruleId", sources.get("ruleId"));
							autofixPlanDet.put("resourceType", sources.get("resourceType"));
							List<Map<String, Object>> planitems = (List<Map<String, Object>>) sources.get("planItems");
							planitems.get(0).entrySet().forEach(item -> {
								if ("plannedActionTime".equalsIgnoreCase(item.getKey())) {
									autofixPlanDet.put("startTime", item.getValue());
								}
								if ("status".equalsIgnoreCase(item.getKey())) {
									autofixPlanDet.put("status", item.getValue().toString().toLowerCase());
								}
							});
							planitems.get(3).entrySet().forEach(item -> {
								if ("plannedActionTime".equalsIgnoreCase(item.getKey())) {
									autofixPlanDet.put("endTime", item.getValue());
								}
								if ("status".equalsIgnoreCase(item.getKey())) {
									autofixPlanDet.put("status", item.getValue().toString().toLowerCase());
								}
							});
							
							List<Map<String, Object>> ruleDetails = new ArrayList<Map<String, Object>>();
					        try {
					        	ruleDetails = rdsRepository.getDataFromPacman("SELECT displayName, policyId FROM cf_RuleInstance WHERE ruleId =\""+sources.get("ruleId")+"\"");
					        	autofixPlanDet.put("ruleName", ruleDetails.get(0).get("displayName"));
					        } catch (Exception exception) {
					            LOGGER.error("Error in getAutofixProjectionDetail for getting rule displayName " , exception);
					        }
					        try {
					        	autofixPlanDet.put("ruleDescription", rdsRepository.queryForString("select policyDesc from cf_Policy WHERE policyId =\""+ruleDetails.get(0).get("policyId")+"\""));
					        } catch (Exception exception) {
					            LOGGER.error("Error in getAutofixProjectionDetail for getting rule description " , exception);
					        }
						}	
						}
					}
				}
		} catch (Exception e) {
			LOGGER.error("Error in getAutofixProjectionDetail", e);
		}
		return autofixPlanDet;
	}
	
	private String filterkey(Map<String, String> filter, String keyText) {
		String searchterm = "";
		if (filter.containsKey(keyText)
				&& StringUtils.isNotBlank(filter.get(keyText))) {
			searchterm = "[";
			String[] splitted = filter.get(keyText).split(",");
			for (String _categoryList : splitted) {
				searchterm = searchterm + "\"" + _categoryList + "\",";
			}
			searchterm = StringUtils.substring(searchterm, 0, searchterm.length() - 1);
			searchterm = searchterm + "]";
		}
		return searchterm;
	}

	@Override
	@SuppressWarnings("unchecked")
	public List<Map<String, Object>> getCloudNotificationsSummary(String assetGroup, boolean globalNotifier,
			String resourceId, String eventStatus) {
		LOGGER.info("Inside getCloudNotificationsSummary");
		List<Map<String, Object>> summaryList = new ArrayList<>();
		try {
			Map<String, Object> countMap = new HashMap<>();
			if (globalNotifier && Strings.isNullOrEmpty(resourceId) && Strings.isNullOrEmpty(resourceId)) {
				countMap.put("globalNotificationsCount", getTotalDocCount("cloud_notifications", "cloud_notification",
						"{\"query\":{\"bool\":{\"must\":[{\"match\":{\"latest\":true}}]}},\"aggs\":{\"name\":{\"terms\":{\"field\":\"eventarn.keyword\",\"size\":1000}}}}"));
				countMap.put("evnetIssuesCount", getTotalDocCount("cloud_notifications", "cloud_notification",
						"{\"size\":0,\"query\":{\"bool\":{\"must\":[{\"match\":{\"latest\":true}},{\"match\": {\"eventtypecategory.keyword\": \"issue\"}}]}},\"aggs\":{\"name\":{\"terms\":{\"field\":\"eventarn.keyword\",\"size\":1000}}}}"));
				countMap.put("eventscheduledCount", getTotalDocCount("cloud_notifications", "cloud_notification",
						"{\"size\":0,\"query\":{\"bool\":{\"must\":[{\"match\":{\"latest\":true}},{\"match\": {\"eventtypecategory.keyword\": \"scheduledChange\"}}]}},\"aggs\":{\"name\":{\"terms\":{\"field\":\"eventarn.keyword\",\"size\":1000}}}}"));
				countMap.put("eventNotificationCount", getTotalDocCount("cloud_notifications", "cloud_notification",
						"{\"size\":0,\"query\":{\"bool\":{\"must\":[{\"match\":{\"latest\":true}},{\"match\": {\"eventtypecategory.keyword\": \"accountNotification\"}}]}},\"aggs\":{\"name\":{\"terms\":{\"field\":\"eventarn.keyword\",\"size\":1000}}}}"));
				countMap.put("autofixCount", 0);
				summaryList.add(countMap);
			} else {
			if(!Strings.isNullOrEmpty(resourceId) && !Strings.isNullOrEmpty(resourceId) && !globalNotifier) {
				String body = "{\"size\": 1,\"_source\":\"eventtypecategory\",\"query\":{\"bool\":{\"must\":[{\"term\":{\"_resourceid.keyword\":\""
						+ resourceId +"\"}},{\"term\":{\"statuscode.keyword\":\""
						+ eventStatus +"\"}}]}}}";
				StringBuilder urlToQuery = new StringBuilder(esUrl).append("/").append(assetGroup).append("/").append(TYPE)
						.append("/").append(_SEARCH);
				Gson gson = new GsonBuilder().create();
				String responseDetails = null;
				try {
					responseDetails = PacHttpUtils.doHttpPost(urlToQuery.toString(), new StringBuilder(body).toString());
				} catch (Exception e) {
					LOGGER.error("Error in getAutofixProjectionDetail", e);
				}
				Map<String, Object> response = (Map<String, Object>) gson.fromJson(responseDetails, Object.class);
				if (response.containsKey(HITS)) {
					Map<String, Object> hits = (Map<String, Object>) response.get(HITS);
					if (hits.containsKey(HITS)) {
						List<Map<String, Object>> hitDetails = (List<Map<String, Object>>) hits.get(HITS);
						if (!hitDetails.isEmpty()) {
							for (Map<String, Object> hitDetail : hitDetails) {
								Map<String, Object> sources = (Map<String, Object>) hitDetail.get(_SOURCE);
								String eventType = sources.get("eventtypecategory").toString();
								switch (eventType) {
								case "scheduledChange":
									countMap.put("globalNotificationsCount", 0);
									countMap.put("evnetIssuesCount", 0);
									countMap.put("eventscheduledCount", sources.size());
									countMap.put("eventNotificationCount", 0);
									countMap.put("autofixCount", 0);
									summaryList.add(countMap);
									break;
								case "issue":
									countMap.put("globalNotificationsCount", 0);
									countMap.put("evnetIssuesCount", sources.size());
									countMap.put("eventscheduledCount", 0);
									countMap.put("eventNotificationCount", 0);
									countMap.put("autofixCount", 0);
									summaryList.add(countMap);
									break;
								case "accountNotification":
									countMap.put("globalNotificationsCount", 0);
									countMap.put("evnetIssuesCount", 0);
									countMap.put("eventscheduledCount", 0);
									countMap.put("eventNotificationCount", sources.size());
									countMap.put("autofixCount", 0);
									summaryList.add(countMap);
									break;
								default:
								}
							}
						}
					}
				}
			} else if(!Strings.isNullOrEmpty(resourceId) && !Strings.isNullOrEmpty(resourceId) && globalNotifier) {
				countMap.put("globalNotificationsCount", 0);
				countMap.put("evnetIssuesCount", 0);
				countMap.put("eventscheduledCount", 0);
				countMap.put("eventNotificationCount", 0);
				countMap.put("autofixCount", 0);
				summaryList.add(countMap);
			} else {
				countMap.put("globalNotificationsCount", getTotalDocCount("cloud_notifications", "cloud_notification",
						"{\"query\":{\"bool\":{\"must\":[{\"match\":{\"latest\":true}}]}},\"aggs\":{\"name\":{\"terms\":{\"field\":\"eventarn.keyword\",\"size\":1000}}}}"));
				countMap.put("evnetIssuesCount", getTotalDocCount(assetGroup, "cloud_notification",
						"{\"size\":0,\"query\":{\"bool\":{\"must\":[{\"match\":{\"latest\":true}},{\"match\": {\"eventtypecategory.keyword\": \"issue\"}}]}},\"aggs\":{\"name\":{\"terms\":{\"field\":\"eventarn.keyword\",\"size\":1000}}}}"));
				countMap.put("eventscheduledCount", getTotalDocCount(assetGroup, "cloud_notification",
						"{\"size\":0,\"query\":{\"bool\":{\"must\":[{\"match\":{\"latest\":true}},{\"match\": {\"eventtypecategory.keyword\": \"scheduledChange\"}}]}},\"aggs\":{\"name\":{\"terms\":{\"field\":\"eventarn.keyword\",\"size\":1000}}}}"));
				countMap.put("eventNotificationCount", getTotalDocCount(assetGroup, "cloud_notification",
						"{\"size\":0,\"query\":{\"bool\":{\"must\":[{\"match\":{\"latest\":true}},{\"match\": {\"eventtypecategory.keyword\": \"accountNotification\"}}]}},\"aggs\":{\"name\":{\"terms\":{\"field\":\"eventarn.keyword\",\"size\":1000}}}}"));
				countMap.put("autofixCount", getAutoFixSummary(assetGroup, "autofixplan"));
				summaryList.add(countMap);
				}
			}
		} catch (Exception e) {
			LOGGER.error("Error in getCloudNotificationsSummary", e);
		}
		LOGGER.info("Exiting getCloudNotificationsSummary");
		return summaryList;
	}
}
