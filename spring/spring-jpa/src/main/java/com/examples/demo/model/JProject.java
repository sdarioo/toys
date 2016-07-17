package com.examples.demo.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Entity implementation class for Entity: JProject
 *
 */
@Entity
@Table(name="projects")
public class JProject extends JObject {
	private static final long serialVersionUID = 1L;
	
	@Column(unique=true)
	private String name;

	public JProject() {
	}
	
	public JProject(String name) {
		this.name = name;
	}
	
	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}
   
}
