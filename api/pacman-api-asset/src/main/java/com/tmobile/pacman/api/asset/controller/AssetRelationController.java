package com.tmobile.pacman.api.asset.controller;
import java.util.List;
import java.util.Map;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.tmobile.pacman.api.asset.domain.ResponseWithCount;
import com.tmobile.pacman.api.asset.service.AssetRelationService;
import com.tmobile.pacman.api.commons.utils.ResponseUtils;
import io.swagger.annotations.ApiOperation;
@RestController
@CrossOrigin
public class AssetRelationController {
	
	@Autowired
    private AssetRelationService assetRelationService;
	
	private static final Log LOGGER = LogFactory.getLog(AssetRelationController.class);
	
	@PreAuthorize("@securityService.hasPermission(authentication, 'ROLE_NW_USER')")
	@ApiOperation(httpMethod = "GET", value = "Get the list of related assets of resource type")
	@GetMapping(value = "/v1/list/assets/relations")
	public ResponseEntity<Object> listRelationAssets(
			@RequestParam(name = "resourceType", required = true) String resourceType,
			@RequestParam(name = "relatedType", required = true) String relatedType,
			@RequestParam(name = "fields", required = false) String fields){
		List<Map<String, Object>> relationAssets;
		try {
			relationAssets = assetRelationService.getRelationAssets(resourceType, relatedType, fields);
		} catch (Exception e) {
			LOGGER.error("Error in listRelationAssets ", e);
			return ResponseUtils.buildFailureResponse(e);
		}		
		return ResponseUtils.buildSucessResponse(new ResponseWithCount(relationAssets, relationAssets.size()));
	}
}



