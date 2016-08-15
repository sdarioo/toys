package com.examples.demo.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

/**
 * Entity implementation class for Entity: JProject
 *
 */
@Entity
@Table(name="projects")
public class JProject extends JObject {
	private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue(generator="projects_id_seq")
	@SequenceGenerator(name = "projects_id_seq", sequenceName = "projects_id_seq", allocationSize=1)
	private Integer id;
	
	@NotNull
	@Column(unique=true)
	private String name;

	protected JProject() {
	}
	
	public JProject(String name) {
		this.name = name;
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
   
}
