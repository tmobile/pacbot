package com.tmobile.pacman.api.admin.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.tmobile.pacman.api.admin.repository.model.ConfigPropertyAudit;

/**
 * The Interface ConfigPropertyAuditRepository.
 */
public interface ConfigPropertyAuditRepository extends JpaRepository<ConfigPropertyAudit, Long>{
	
	/**
	 * Find all by order by modified date desc modified by asc.
	 *
	 * @return the list
	 */
	List<ConfigPropertyAudit> findAllByOrderByModifiedDateDescModifiedByAsc();

}
