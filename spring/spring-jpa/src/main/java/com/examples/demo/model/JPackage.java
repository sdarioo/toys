package com.examples.demo.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

/**
 * Entity implementation class for Entity: JPackage
 *
 */
@Entity
@Table(name="packages")
public class JPackage extends JObject {
	private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue(generator="packages_id_seq")
	@SequenceGenerator(name = "packages_id_seq", sequenceName = "packages_id_seq", allocationSize=1)
	private Integer id;

	@Column(unique=true)
	private String name;
	
	@ManyToOne
	@JoinColumn(name="project_id", nullable=false)
	private JProject project;

	@ManyToOne
	@JoinColumn(name="parent_id", nullable=true)
	private JPackage parent;
	
	
	public JPackage() {
	}
	
	public JPackage(String name, JProject project, JPackage parent) {
		this.name = name;
		this.project = project;
		this.parent = parent;
	}
	
	public Integer getId() {
		return id;
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
