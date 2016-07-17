package com.examples.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.examples.demo.model.JProject;

public interface ProjectRepository extends JpaRepository<JProject, Integer> {

}
