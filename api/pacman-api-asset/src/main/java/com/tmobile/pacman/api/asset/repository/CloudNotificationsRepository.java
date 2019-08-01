package com.tmobile.pacman.api.asset.repository;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

/**
 * This is the Cloud Notifications Repository layer which makes call to RDS DB as well as ElasticSearch
 */
@Repository
public interface CloudNotificationsRepository {
	
	public List<Map<String, Object>> getNotifications(String assetGroup, Map<String, String> filter, boolean globalNotifier, int size, int from);
	public List<Map<String, Object>> getCloudNotificationsSummary(String assetGroup, boolean globalNotifier, String resourceId, String eventStatus);
	public List<Map<String, Object>> getCloudNotificationDetail(String eventArn, boolean globalNotifier, String assetGroup);
	public Map<String, Object> getCloudNotificationInfo(String eventArn, boolean globalNotifier, String assetGroup);
	public Map<String, Object> getAutofixProjectionDetail(String assetGroup, Map<String, String> filter);

}
