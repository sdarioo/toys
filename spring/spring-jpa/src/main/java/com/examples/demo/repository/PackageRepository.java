package com.examples.demo.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.examples.demo.model.JPackage;
import com.examples.demo.model.JProject;

public interface PackageRepository extends JpaRepository<JPackage, Integer> {
	
	@Query("SELECT p FROM JPackage p join fetch p.project WHERE p.project = :project and p.parent IS NULL")
	List<JPackage> findRootPackages(@Param("project") JProject project);
	
	@Query("SELECT p FROM JPackage p join fetch p.parent join fetch p.project WHERE p.parent = :parent")
	List<JPackage> findChildPackages(@Param("parent") JPackage parent);
	
}
