package com.tmobile.pacman.api.asset.repository;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.tmobile.pacman.api.commons.Constants;
import com.tmobile.pacman.api.commons.exception.DataException;
import com.tmobile.pacman.api.commons.repo.ElasticSearchRepository;
import com.tmobile.pacman.api.commons.repo.PacmanRdsRepository;

@Repository
public class AssetRelationRepository {
	
	@Autowired
	private ElasticSearchRepository esRepository;
	
	@Autowired
	PacmanRdsRepository rdsRepository;
	
	private static final Log LOGGER = LogFactory.getLog(AssetRelationRepository.class);

	public List<Map<String, Object>> getRelationAssets(String dataSource,String resourceType, String relation, List<String> fields) throws DataException {
		
		try {
			return esRepository.getSortedDataFromES(dataSource + "_" + resourceType, resourceType +"_"+ relation,null, null, null, fields, null, null);
		} catch (Exception e) {
			LOGGER.error("Error in getRelationAssets", e);
			throw new DataException(e);
		}
	}
	
	public boolean isTargetTypeExists(String resourceType) {
		
		String query = "SELECT DISTINCT targetName AS TYPE FROM cf_Target";
		List<String> targetTypes = rdsRepository.getDataFromPacman(query).stream().map(obj -> obj.get(Constants.TYPE).toString())
				.collect(Collectors.toList());
		return targetTypes.contains(resourceType);
	}
}
