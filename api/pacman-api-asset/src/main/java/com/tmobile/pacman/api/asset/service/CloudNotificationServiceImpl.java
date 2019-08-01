package com.tmobile.pacman.api.asset.service;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.tmobile.pacman.api.asset.repository.CloudNotificationsRepository;

/**
 * Implemented class for CloudNotificationService and all its method
 */
@Service
public class CloudNotificationServiceImpl implements CloudNotificationService {
	
	 @Autowired
	 private CloudNotificationsRepository repository;

	@Override
	public List<Map<String, Object>> getNotifications(String assetGroup, Map<String, String> filter,
			boolean globalNotifier, int size, int from) {
		return repository.getNotifications(assetGroup, filter, globalNotifier, size, from);
	}

	@Override
	public List<Map<String, Object>> getCloudNotificationsSummary(String assetGroup, boolean globalNotifier, String resourceId, String eventStatus) {
		return repository.getCloudNotificationsSummary(assetGroup, globalNotifier, resourceId, eventStatus);
	}

	@Override
	public List<Map<String, Object>> getCloudNotificationDetail(String eventArn, boolean globalNotifier, String assetGroup) {
		return repository.getCloudNotificationDetail(eventArn, globalNotifier, assetGroup);
	}

	@Override
	public Map<String, Object> getCloudNotificationInfo(String eventArn, boolean globalNotifier, String assetGroup) {
		return repository.getCloudNotificationInfo(eventArn, globalNotifier, assetGroup);
	}

	@Override
	public Map<String, Object> getAutofixProjectionDetail(String assetGroup, Map<String, String> filter) {
		return repository.getAutofixProjectionDetail(assetGroup, filter);
	}

}
