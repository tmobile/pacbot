package com.tmobile.pacman.api.admin.repository.service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.tmobile.pacman.api.admin.common.AdminConstants;
import com.tmobile.pacman.api.admin.domain.ConfigPropertyAuditItem;
import com.tmobile.pacman.api.admin.domain.ConfigPropertyAuditTrail;
import com.tmobile.pacman.api.admin.domain.ConfigPropertyDataChange;
import com.tmobile.pacman.api.admin.domain.ConfigPropertyItem;
import com.tmobile.pacman.api.admin.domain.ConfigPropertyNode;
import com.tmobile.pacman.api.admin.domain.ConfigPropertyRequest;
import com.tmobile.pacman.api.admin.domain.ConfigPropertyRollBackItem;
import com.tmobile.pacman.api.admin.domain.ConfigPropertyRollbackPreview;
import com.tmobile.pacman.api.admin.domain.ConfigTreeNode;
import com.tmobile.pacman.api.admin.repository.ConfigPropertyAuditRepository;
import com.tmobile.pacman.api.admin.repository.ConfigPropertyMetadataRepository;
import com.tmobile.pacman.api.admin.repository.ConfigPropertyRelationRepository;
import com.tmobile.pacman.api.admin.repository.ConfigPropertyRepository;
import com.tmobile.pacman.api.admin.repository.model.ConfigProperty;
import com.tmobile.pacman.api.admin.repository.model.ConfigPropertyAudit;
import com.tmobile.pacman.api.admin.repository.model.ConfigPropertyKey;
import com.tmobile.pacman.api.admin.repository.model.ConfigPropertyMetadata;
import com.tmobile.pacman.api.admin.repository.model.ConfigPropertyRelation;
import com.tmobile.pacman.api.admin.util.AdminUtils;

/**
 * The Class ConfigPropertyServiceImpl.
 */
@Component
public class ConfigPropertyServiceImpl implements ConfigPropertyService {
	
	/** The Constant DATE_FORMAT. */
	private static final String DATE_FORMAT = "MM/dd/yyyy HH:mm:ss";

	/** The config property repository. */
	@Autowired
	ConfigPropertyRepository configPropertyRepository;

	/** The config property relation repository. */
	@Autowired
	ConfigPropertyRelationRepository configPropertyRelationRepository;

	/** The config property metadata repository. */
	@Autowired
	ConfigPropertyMetadataRepository configPropertyMetadataRepository;

	/** The config property audit repository. */
	@Autowired
	ConfigPropertyAuditRepository configPropertyAuditRepository;

	/** The active profile. */
	@Value("${spring.profiles.active}")
	private String activeProfile;

	/* (non-Javadoc)
	 * @see com.tmobile.pacman.api.admin.repository.service.ConfigPropertyService#listProperties()
	 */
	@Override
	public ConfigTreeNode listProperties() throws Exception {
		List<ConfigProperty> configPropertyList = configPropertyRepository.findAll();
		List<ConfigPropertyRelation> configRelationList = configPropertyRelationRepository.findAll();
		List<ConfigPropertyMetadata> configMetadataList = configPropertyMetadataRepository.findAll();

		List<String> rootNode = new ArrayList<>();

		Map<String, String> applicationParentMap = new HashMap<>();
		configRelationList.forEach(configRelation -> {
			applicationParentMap.put(configRelation.getApplication(), configRelation.getParent());
			if ("root".equals(configRelation.getParent())) {
				rootNode.add(configRelation.getApplication());
			}
		});

		ConfigTreeNode treeNode = new ConfigTreeNode();
		treeNode.setName(rootNode.get(0));

		populateNode(treeNode, applicationParentMap, configPropertyList, configMetadataList);

		return treeNode;
	}

	/**
	 * Populate node.
	 *
	 * @param treeNode the tree node
	 * @param applicationParentMap the application parent map
	 * @param configPropertyList the config property list
	 * @param configMetadataList the config metadata list
	 */
	private void populateNode(ConfigTreeNode treeNode, Map<String, String> applicationParentMap,
			List<ConfigProperty> configPropertyList, List<ConfigPropertyMetadata> configMetadataList) {
		populateLeafNodes(treeNode, configPropertyList, configMetadataList);
		populateChildNodes(treeNode, applicationParentMap, configPropertyList, configMetadataList);

	}

	/**
	 * Populate leaf nodes.
	 *
	 * @param treeNode the tree node
	 * @param configPropertyList the config property list
	 * @param configMetadataList the config metadata list
	 */
	private void populateLeafNodes(ConfigTreeNode treeNode,
			List<ConfigProperty> configPropertyList, List<ConfigPropertyMetadata> configMetadataList) {

		Map<String, String> cfkeyDescriptionMap = new HashMap<>();
		configMetadataList.forEach(configMetadata -> {
			cfkeyDescriptionMap.put(configMetadata.getCfkey(), configMetadata.getDescription());
		});

		List<ConfigPropertyNode> valueNodesList = new ArrayList<>();
		configPropertyList.forEach(configProperty -> {
			if (configProperty.getConfigKeyParams().getApplication().equals(treeNode.getName())) {
				ConfigPropertyNode valueNode = new ConfigPropertyNode();
				valueNode.setKey(configProperty.getConfigKeyParams().getCfkey());
				valueNode.setDescription(cfkeyDescriptionMap.get(configProperty.getConfigKeyParams().getCfkey()));
				valueNode.setValue(configProperty.getValue());
				valueNodesList.add(valueNode);
			}
		});
		treeNode.setProperties(valueNodesList);

	}

	/**
	 * Populate child nodes.
	 *
	 * @param treeNode the tree node
	 * @param applicationParentMap the application parent map
	 * @param configPropertyList the config property list
	 * @param configMetadataList the config metadata list
	 */
	private void populateChildNodes(ConfigTreeNode treeNode, Map<String, String> applicationParentMap,
			List<ConfigProperty> configPropertyList, List<ConfigPropertyMetadata> configMetadataList) {

		List<String> childAppStrings = new ArrayList<>();
		applicationParentMap.forEach((application, parent) -> {
			if (parent.equals(treeNode.getName())) {
				childAppStrings.add(application);
			}
		});
		List<ConfigTreeNode> childNodes = new ArrayList<>();
		childAppStrings.forEach(childAppString -> {
			ConfigTreeNode childNode = new ConfigTreeNode();
			childNode.setName(childAppString);
			populateNode(childNode, applicationParentMap, configPropertyList, configMetadataList);
			childNodes.add(childNode);
		});
		treeNode.setChildren(childNodes);
	}

	/* (non-Javadoc)
	 * @see com.tmobile.pacman.api.admin.repository.service.ConfigPropertyService#addUpdateProperties(com.tmobile.pacman.api.admin.domain.ConfigPropertyRequest, java.lang.String, java.lang.String, java.lang.String, boolean)
	 */
	@Override
	@Transactional
	public List<ConfigProperty> addUpdateProperties(ConfigPropertyRequest configPropertyRequest, String user,
			String userMessage, String timeNow, boolean partOfRollbackOperation) {
		List<ConfigProperty> configPropertyList = new ArrayList<>();
		List<ConfigPropertyAudit> configPropertyAuditList = new ArrayList<>();

		configPropertyRequest.getConfigProperties().forEach(configPropertyItem -> {
			ConfigPropertyKey configPropertyKey = new ConfigPropertyKey();
			configPropertyKey.setApplication(configPropertyItem.getApplication());
			configPropertyKey.setProfile(activeProfile);
			configPropertyKey.setLabel(AdminConstants.LATEST);
			configPropertyKey.setCfkey(configPropertyItem.getConfigKey());

			if (configPropertyRepository.existsByConfigKeyParams(configPropertyKey)) {
				ConfigProperty existingConfigProperty = configPropertyRepository
						.findByConfigKeyParams(configPropertyKey);

				ConfigPropertyAudit configPropertyAudit = new ConfigPropertyAudit();
				configPropertyAudit.setId(UUID.randomUUID().toString());
				configPropertyAudit.setCfkey(existingConfigProperty.getConfigKeyParams().getCfkey());
				configPropertyAudit.setApplication(existingConfigProperty.getConfigKeyParams().getApplication());
				configPropertyAudit.setProfile(existingConfigProperty.getConfigKeyParams().getProfile());
				configPropertyAudit.setLabel(existingConfigProperty.getConfigKeyParams().getLabel());
				configPropertyAudit.setOldvalue(existingConfigProperty.getValue());
				configPropertyAudit.setNewvalue(configPropertyItem.getConfigValue());
				configPropertyAudit.setModifiedBy(user);
				configPropertyAudit.setModifiedDate(timeNow);
				configPropertyAudit.setUserMessage(userMessage);
				configPropertyAudit.setSystemMessage("PUT operation through API invocation");
				if (partOfRollbackOperation) {
					configPropertyAudit.setSystemMessage(AdminConstants.CONFIG_ROLLBACK_MSG);
				}
				configPropertyAuditList.add(configPropertyAudit);

				existingConfigProperty.setValue(configPropertyItem.getConfigValue());
				existingConfigProperty.setModifiedBy(user);
				existingConfigProperty.setModifiedDate(timeNow);
				configPropertyList.add(existingConfigProperty);

			} else {
				ConfigProperty configProperty = new ConfigProperty();
				configProperty.setConfigKeyParams(configPropertyKey);
				configProperty.setValue(configPropertyItem.getConfigValue());
				configProperty.setCreatedBy(user);
				configProperty.setCreatedDate(timeNow);
				configPropertyList.add(configProperty);

				ConfigPropertyAudit configPropertyAudit = new ConfigPropertyAudit();
				configPropertyAudit.setId(UUID.randomUUID().toString());
				configPropertyAudit.setCfkey(configPropertyKey.getCfkey());
				configPropertyAudit.setApplication(configPropertyKey.getApplication());
				configPropertyAudit.setProfile(configPropertyKey.getProfile());
				configPropertyAudit.setLabel(configPropertyKey.getLabel());
				configPropertyAudit.setNewvalue(configPropertyItem.getConfigValue());
				configPropertyAudit.setModifiedBy(user);
				configPropertyAudit.setModifiedDate(timeNow);
				configPropertyAudit.setUserMessage(userMessage);
				configPropertyAudit.setSystemMessage("POST operation through API invocation");
				if (partOfRollbackOperation) {
					configPropertyAudit.setSystemMessage(AdminConstants.CONFIG_ROLLBACK_MSG);
				}
				configPropertyAuditList.add(configPropertyAudit);
			}
		});

		List<ConfigProperty> configPropertyResult = configPropertyRepository.saveAll(configPropertyList);
		configPropertyAuditRepository.saveAll(configPropertyAuditList);
		return configPropertyResult;
	}

	/* (non-Javadoc)
	 * @see com.tmobile.pacman.api.admin.repository.service.ConfigPropertyService#deleteProperty(com.tmobile.pacman.api.admin.domain.ConfigPropertyItem, java.lang.String, java.lang.String, java.lang.String, boolean)
	 */
	@Override
	@Transactional
	public String deleteProperty(ConfigPropertyItem configPropertyItem, String user, String userMessage, String timeNow,
			boolean partOfRollbackOperation) {
		ConfigPropertyKey configPropertyKey = new ConfigPropertyKey();
		configPropertyKey.setApplication(configPropertyItem.getApplication());
		configPropertyKey.setProfile(activeProfile);
		configPropertyKey.setLabel(AdminConstants.LATEST);
		configPropertyKey.setCfkey(configPropertyItem.getConfigKey());

		ConfigProperty configProperty = new ConfigProperty();
		configProperty.setConfigKeyParams(configPropertyKey);
		configProperty.setValue(configPropertyItem.getConfigValue());

		ConfigPropertyAudit configPropertyAudit = new ConfigPropertyAudit();
		configPropertyAudit.setId(UUID.randomUUID().toString());
		configPropertyAudit.setCfkey(configPropertyKey.getCfkey());
		configPropertyAudit.setApplication(configPropertyKey.getApplication());
		configPropertyAudit.setProfile(configPropertyKey.getProfile());
		configPropertyAudit.setLabel(configPropertyKey.getLabel());
		configPropertyAudit.setOldvalue(configPropertyRepository.findByConfigKeyParams(configPropertyKey).getValue());
		configPropertyAudit.setModifiedBy(user);
		configPropertyAudit.setModifiedDate(timeNow);
		configPropertyAudit.setUserMessage(userMessage);
		configPropertyAudit.setSystemMessage("DELETE operation through API invocation");

		if (partOfRollbackOperation) {
			configPropertyAudit.setSystemMessage(AdminConstants.CONFIG_ROLLBACK_MSG);
		}
		configPropertyRepository.delete(configProperty);
		configPropertyAuditRepository.save(configPropertyAudit);

		return "Success";
	}

	/* (non-Javadoc)
	 * @see com.tmobile.pacman.api.admin.repository.service.ConfigPropertyService#isAllPropertiesExisting(com.tmobile.pacman.api.admin.domain.ConfigPropertyRequest)
	 */
	@Override
	public boolean isAllPropertiesExisting(ConfigPropertyRequest configPropertyRequest) throws Exception {
		List<String> invalidList = new ArrayList<>();
		configPropertyRequest.getConfigProperties().forEach(configPropertyItem -> {
			try {
				if (!isPropertyExisting(configPropertyItem)) {
					invalidList.add(configPropertyItem.toString());
				}
			} catch (Exception e) {
				// Error in bool check
			}
		});

		return invalidList.isEmpty();
	}

	/* (non-Javadoc)
	 * @see com.tmobile.pacman.api.admin.repository.service.ConfigPropertyService#isAnyPropertyExisting(com.tmobile.pacman.api.admin.domain.ConfigPropertyRequest)
	 */
	@Override
	public boolean isAnyPropertyExisting(ConfigPropertyRequest configPropertyRequest) throws Exception {
		List<String> validList = new ArrayList<>();
		configPropertyRequest.getConfigProperties().forEach(configPropertyItem -> {
			try {
				if (isPropertyExisting(configPropertyItem)) {
					validList.add(configPropertyItem.toString());
				}
			} catch (Exception e) {
				// Error in bool check
			}
		});

		return !validList.isEmpty();
	}

	/* (non-Javadoc)
	 * @see com.tmobile.pacman.api.admin.repository.service.ConfigPropertyService#isPropertyExisting(com.tmobile.pacman.api.admin.domain.ConfigPropertyItem)
	 */
	@Override
	public boolean isPropertyExisting(ConfigPropertyItem configPropertyItem) throws Exception {
		List<ConfigProperty> configPropertyList = configPropertyRepository.findAll();
		List<ConfigProperty> matchList = new ArrayList<>();
		configPropertyList.forEach(existingConfigProperty -> {
			if (existingConfigProperty.toString().equals(configPropertyItem.toString() + "|" + activeProfile)) {
				matchList.add(existingConfigProperty);
			}
		});
		return matchList.size() > 0;
	}

	/* (non-Javadoc)
	 * @see com.tmobile.pacman.api.admin.repository.service.ConfigPropertyService#isCfkeyExisting(java.lang.String)
	 */
	@Override
	public boolean isCfkeyExisting(String cfkey) throws Exception {
		List<ConfigPropertyMetadata> configMetadataList = configPropertyMetadataRepository.findAll();
		Map<String, String> cfkeyDescriptionMap = new HashMap<>();
		configMetadataList.forEach(configMetadata -> {
			cfkeyDescriptionMap.put(configMetadata.getCfkey(), configMetadata.getDescription());
		});
		if (cfkeyDescriptionMap.containsKey(cfkey)) {
			return true;
		} else {
			return false;
		}
	}

	/* (non-Javadoc)
	 * @see com.tmobile.pacman.api.admin.repository.service.ConfigPropertyService#isAllCfkeysExisting(com.tmobile.pacman.api.admin.domain.ConfigPropertyRequest)
	 */
	@Override
	public boolean isAllCfkeysExisting(ConfigPropertyRequest configPropertyRequest) throws Exception {
		List<String> invalidKeys = new ArrayList<>();

		List<ConfigPropertyMetadata> configMetadataList = configPropertyMetadataRepository.findAll();
		Map<String, String> cfkeyDescriptionMap = new HashMap<>();
		configMetadataList.forEach(configMetadata -> {
			cfkeyDescriptionMap.put(configMetadata.getCfkey(), configMetadata.getDescription());
		});
		configPropertyRequest.getConfigProperties().forEach(configPropertyItem -> {
			if (!cfkeyDescriptionMap.containsKey(configPropertyItem.getConfigKey())) {
				invalidKeys.add(configPropertyItem.getConfigKey());
			}
		});

		return invalidKeys.isEmpty();
	}

	/* (non-Javadoc)
	 * @see com.tmobile.pacman.api.admin.repository.service.ConfigPropertyService#isApplicationExisting(java.lang.String)
	 */
	@Override
	public boolean isApplicationExisting(String application) throws Exception {
		List<ConfigPropertyRelation> configRelationList = configPropertyRelationRepository.findAll();
		Map<String, String> applicationParentMap = new HashMap<>();
		configRelationList.forEach(configRelation -> {
			applicationParentMap.put(configRelation.getApplication(), configRelation.getParent());

		});
		if (applicationParentMap.containsKey(application)) {
			return true;
		}
		return false;
	}

	/* (non-Javadoc)
	 * @see com.tmobile.pacman.api.admin.repository.service.ConfigPropertyService#isAllApplicationsExisting(com.tmobile.pacman.api.admin.domain.ConfigPropertyRequest)
	 */
	@Override
	public boolean isAllApplicationsExisting(ConfigPropertyRequest configPropertyRequest) throws Exception {

		List<String> invalidApplications = new ArrayList<>();
		List<ConfigPropertyRelation> configRelationList = configPropertyRelationRepository.findAll();
		Map<String, String> applicationParentMap = new HashMap<>();
		configRelationList.forEach(configRelation -> {
			applicationParentMap.put(configRelation.getApplication(), configRelation.getParent());

		});
		configPropertyRequest.getConfigProperties().forEach(configPropertyItem -> {
			if (!applicationParentMap.containsKey(configPropertyItem.getApplication())) {
				invalidApplications.add(configPropertyItem.getApplication());
			}
		});

		return invalidApplications.isEmpty();
	}

	/* (non-Javadoc)
	 * @see com.tmobile.pacman.api.admin.repository.service.ConfigPropertyService#listAllKeys()
	 */
	@Override
	public List<String> listAllKeys() throws Exception {
		List<ConfigPropertyMetadata> configMetaData = configPropertyMetadataRepository.findAll();
		List<String> keyList = new ArrayList<>();
		configMetaData.forEach(configKeyInfo -> keyList.add(configKeyInfo.getCfkey()));
		return keyList;
	}

	/* (non-Javadoc)
	 * @see com.tmobile.pacman.api.admin.repository.service.ConfigPropertyService#listAllConfigPropertyAudits(java.lang.String)
	 */
	@Override
	public ConfigPropertyAuditTrail listAllConfigPropertyAudits(String timestamp) throws Exception {

		if (StringUtils.isEmpty(timestamp)) {
			LocalDateTime now = LocalDateTime.now();
			timestamp = now.minusMonths(1).format(DateTimeFormatter.ofPattern(DATE_FORMAT));
		}

		List<ConfigPropertyAudit> allStoredAudits = configPropertyAuditRepository
				.findAllByOrderByModifiedDateDescModifiedByAsc();

		Comparator<ConfigPropertyAudit> comp = ((m2, m1) -> LocalDateTime
	                .parse(m1.getModifiedDate(), DateTimeFormatter.ofPattern(DATE_FORMAT)).compareTo(
	                        LocalDateTime.parse(m2.getModifiedDate(), DateTimeFormatter.ofPattern(DATE_FORMAT))));
	    Collections.sort(allStoredAudits, comp);
		
		ConfigPropertyAuditItem previousAuditItem = new ConfigPropertyAuditItem();
		ConfigPropertyAuditItem currentAuditItem = new ConfigPropertyAuditItem();
		List<ConfigPropertyDataChange> dataChangeList = new ArrayList<ConfigPropertyDataChange>();

		List<ConfigPropertyAuditItem> auditItemList = new ArrayList<>();

		// If the timestamp as well as the modified user is the same, we assume that the
		// records are part of the same commit
		for (ConfigPropertyAudit storedAudit : allStoredAudits) {
			if (LocalDateTime.parse(storedAudit.getModifiedDate(), DateTimeFormatter.ofPattern(DATE_FORMAT))
					.isBefore(LocalDateTime.parse(timestamp, DateTimeFormatter.ofPattern(DATE_FORMAT)))) {
				break;
			}

			if (!(storedAudit.getModifiedDate().equals(previousAuditItem.getAuditTimeStamp())
					&& storedAudit.getModifiedBy().equals(previousAuditItem.getModifiedBy()))) {

				if (currentAuditItem.getAuditTimeStamp() != null) {
					auditItemList.add(currentAuditItem);
					currentAuditItem = new ConfigPropertyAuditItem();
					dataChangeList = new ArrayList<ConfigPropertyDataChange>();
				}

			}
			currentAuditItem.setConfigPropertyChangeList(dataChangeList);
			currentAuditItem.setAuditTimeStamp(storedAudit.getModifiedDate());
			currentAuditItem.setModifiedBy(storedAudit.getModifiedBy());
			currentAuditItem.setUserMessage(storedAudit.getUserMessage());
			currentAuditItem.setSystemMessage(storedAudit.getSystemMessage());

			ConfigPropertyDataChange dataChange = new ConfigPropertyDataChange();
			dataChange.setConfigKey(storedAudit.getCfkey());
			dataChange.setApplication(storedAudit.getApplication());
			dataChange.setOldConfigValue(storedAudit.getOldvalue());
			dataChange.setNewConfigValue(storedAudit.getNewvalue());
			dataChangeList.add(dataChange);
			previousAuditItem = currentAuditItem;

		}
		// The leftover item which got missed in the iteration
		if (currentAuditItem.getAuditTimeStamp() != null) {
			auditItemList.add(currentAuditItem);
		}

		ConfigPropertyAuditTrail trail = new ConfigPropertyAuditTrail();

		trail.setConfigPropertyAudit(auditItemList);

		return trail;
	}

	/* (non-Javadoc)
	 * @see com.tmobile.pacman.api.admin.repository.service.ConfigPropertyService#getRollbackPreview(java.lang.String)
	 */
	@Override
	public ConfigPropertyRollbackPreview getRollbackPreview(String timestamp) throws Exception {

		ConfigPropertyAuditTrail trail = listAllConfigPropertyAudits(timestamp);
		List<ConfigPropertyRollBackItem> rollBackItemList = new ArrayList<>();

		for (ConfigPropertyAuditItem auditItem : trail.getConfigPropertyAudit()) {
			for (ConfigPropertyDataChange changeItem : auditItem.getConfigPropertyChangeList()) {

				ConfigPropertyRollBackItem existingRollBackItem = getRollBackItemIfExists(rollBackItemList, changeItem);

				if (null == existingRollBackItem) {
					ConfigPropertyRollBackItem rollBackItem = new ConfigPropertyRollBackItem();
					rollBackItem.setApplication(changeItem.getApplication());
					rollBackItem.setConfigKey(changeItem.getConfigKey());
					rollBackItem.setPresentConfigValue(
							changeItem.getNewConfigValue() == null ? "" : changeItem.getNewConfigValue());
					rollBackItem.setFutureConfigValue(
							changeItem.getOldConfigValue() == null ? "" : changeItem.getOldConfigValue());
					rollBackItemList.add(rollBackItem);
				} else {
					existingRollBackItem.setFutureConfigValue(
							changeItem.getOldConfigValue() == null ? "" : changeItem.getOldConfigValue());
				}

			}

		}
		// If any trails got missed out by someone doing manual updates in RDS, the
		// audit trail may not match exactly with the present state of the DB
		List<ConfigPropertyRollBackItem> refinedRollBackItemList = syncWithPresentSystemState(rollBackItemList);

		ConfigPropertyRollbackPreview preview = new ConfigPropertyRollbackPreview();
		preview.setRollbackChangeSet(refinedRollBackItemList);
		preview.setRestoreToTimestamp(timestamp);

		return preview;

	}

	/**
	 * Sync with present system state.
	 *
	 * @param rollBackItemList the roll back item list
	 * @return the list
	 */
	private List<ConfigPropertyRollBackItem> syncWithPresentSystemState(
			List<ConfigPropertyRollBackItem> rollBackItemList) {

		List<ConfigPropertyRollBackItem> syncedUpRollBackItemList = new ArrayList<>();

		for (ConfigPropertyRollBackItem rollBackItem : rollBackItemList) {
			ConfigPropertyKey configPropertyKey = new ConfigPropertyKey();
			configPropertyKey.setApplication(rollBackItem.getApplication());
			configPropertyKey.setProfile(activeProfile);
			configPropertyKey.setLabel(AdminConstants.LATEST);
			configPropertyKey.setCfkey(rollBackItem.getConfigKey());
			ConfigProperty presentConfigPropertyAsPerSystemState = configPropertyRepository
					.findByConfigKeyParams(configPropertyKey);
			String presentValueAsPerSystemState = presentConfigPropertyAsPerSystemState == null ? ""
					: configPropertyRepository.findByConfigKeyParams(configPropertyKey).getValue();

			// Check system state. THe present value as per system state needs to be shown
			// as 'present' value in the rollback preview, else user might get confused
			rollBackItem.setPresentConfigValue(presentValueAsPerSystemState);

			// if presnt and future are same, why bother? lets NOT add it to our list
			if (!presentValueAsPerSystemState.equals(rollBackItem.getFutureConfigValue())) {
				syncedUpRollBackItemList.add(rollBackItem);
			}

		}
		return syncedUpRollBackItemList;
	}

	/**
	 * Gets the roll back item if exists.
	 *
	 * @param rollBackItemList the roll back item list
	 * @param changeItem the change item
	 * @return the roll back item if exists
	 */
	private ConfigPropertyRollBackItem getRollBackItemIfExists(List<ConfigPropertyRollBackItem> rollBackItemList,
			ConfigPropertyDataChange changeItem) {
		for (ConfigPropertyRollBackItem rollBackItem : rollBackItemList) {
			if (changeItem.getApplication().equals(rollBackItem.getApplication())
					&& changeItem.getConfigKey().equals(rollBackItem.getConfigKey())) {
				return rollBackItem;
			}
		}
		return null;
	}

	/* (non-Javadoc)
	 * @see com.tmobile.pacman.api.admin.repository.service.ConfigPropertyService#doConfigPropertyRollbackToTimestamp(java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	@Transactional
	public String doConfigPropertyRollbackToTimestamp(String timestamp, String user, String userMessage)
			throws Exception {
		ConfigPropertyRollbackPreview preview = getRollbackPreview(timestamp);
		String timeNow = AdminUtils.getFormatedStringDate(DATE_FORMAT, new Date());

		preview.getRollbackChangeSet().forEach(configPropertyRollBackItem -> {
			ConfigPropertyItem item = new ConfigPropertyItem();
			item.setApplication(configPropertyRollBackItem.getApplication());
			item.setConfigKey(configPropertyRollBackItem.getConfigKey());
			item.setConfigValue(configPropertyRollBackItem.getFutureConfigValue());

			if (StringUtils.isEmpty(configPropertyRollBackItem.getFutureConfigValue())) {
				deleteProperty(item, user, userMessage, timeNow, true);
			} else {
				List<ConfigPropertyItem> itemList = new ArrayList<>();
				itemList.add(item);
				ConfigPropertyRequest req = new ConfigPropertyRequest();
				req.setConfigProperties(itemList);

				addUpdateProperties(req, user, userMessage, timeNow, true);
			}

		});

		return "Success";
	}

    /* (non-Javadoc)
     * @see com.tmobile.pacman.api.admin.repository.service.ConfigPropertyService#listProperty(java.lang.String, java.lang.String)
     */
    @Override
    public List<ConfigProperty> listProperty(String cfkey, String application) {
        List<ConfigProperty> configPropertyList = configPropertyRepository.findAll();
        List<ConfigProperty> matchList = new ArrayList<>();
        configPropertyList.forEach(existingConfigProperty -> {
            if (existingConfigProperty.getConfigKeyParams().getCfkey().equals(cfkey)) {
                if (StringUtils.isEmpty(application) || application.equals(existingConfigProperty.getConfigKeyParams().getApplication())) {
                    matchList.add(existingConfigProperty);
                }
            }
        });
        return matchList;
    }
}
