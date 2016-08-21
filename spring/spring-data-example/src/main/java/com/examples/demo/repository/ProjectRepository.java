package com.examples.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.examples.demo.model.JProject;
import com.examples.demo.repository.custom.ProjectRepositoryCustom;

public interface ProjectRepository extends JpaRepository<JProject, Integer>, ProjectRepositoryCustom {
}
