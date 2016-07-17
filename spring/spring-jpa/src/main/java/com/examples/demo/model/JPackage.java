package com.examples.demo.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 * Entity implementation class for Entity: JPackage
 *
 */
@Entity
@Table(name="packages")
public class JPackage extends JObject {
	private static final long serialVersionUID = 1L;
	
	@Column(unique=true)
	private String name;
	
	@ManyToOne
	@JoinColumn(name="parent_proj_id", nullable=false)
	private JProject project;

	@ManyToOne
	@JoinColumn(name="parent_pkg_id", nullable=true)
	private JPackage parent;
	
	
	public JPackage() {
	}
	
	public JPackage(String name, JProject project, JPackage parent) {
		this.name = name;
		this.project = project;
		this.parent = parent;
	}
	   
	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	public JProject getProject() {
		return project;
	}

	public void setProject(JProject project) {
		this.project = project;
	}

	public JPackage getParent() {
		return parent;
	}

	public void setParent(JPackage parent) {
		this.parent = parent;
	}
   
}
