package com.tmobile.pacman.api.admin.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.tmobile.pacman.api.admin.repository.model.RuleCategory;

/**
 * The Interface RuleCategoryRepository.
 */
@Repository
public interface RuleCategoryRepository extends JpaRepository<RuleCategory, String> {

}
