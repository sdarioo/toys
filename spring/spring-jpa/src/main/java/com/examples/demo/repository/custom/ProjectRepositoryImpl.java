package com.examples.demo.repository.custom;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import com.examples.demo.model.JProject;

/**
 * Spring naming convention requires that custom repository follows naming rule:
 * {RepoName}Impl implements {RepoName}Custom 
 */
public class ProjectRepositoryImpl implements ProjectRepositoryCustom {

	private final EntityManager em;
	
	@Autowired
	public ProjectRepositoryImpl(EntityManager em)
	{
		this.em = em;
	}
	
	@Override
	@Transactional
	public List<JProject> saveAndFlush(Iterable<JProject> entities) 
	{
		List<JProject> result = new ArrayList<>();
		for (JProject p : entities) {
			if (p.isNew()) {
				em.persist(p);
			} else {
				em.merge(p);
			}
			result.add(p);
		}
		em.flush();
		em.clear();
		
		return result;
	}

}
