package com.examples.demo.repository.custom;

import java.util.List;

import com.examples.demo.model.JProject;

/** 
 * Custom extensions to ProjectRepository. 
 * 
 * Spring naming convention requires that custom repository follows naming rule:
 * {RepoName}Impl implements {RepoName}Custom 
 */ 
public interface ProjectRepositoryCustom {

	/**
	 * Save provided entities in single batch operation flushing and clearing entity manager at the end.
	 * Make sure that number of entities to save is equal to hibernate.jdbc.batch_size property value.
	 * 
	 * @param entities projects to save
	 * @return saved projects
	 */
	List<JProject> saveAndFlush(Iterable<JProject> entities);
}
