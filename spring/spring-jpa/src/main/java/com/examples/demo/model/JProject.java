package com.examples.demo.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

/**
 * Entity implementation class for Entity: JProject
 *
 */
@Entity
@Table(name="projects")
public class JProject extends JObject {
	private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
//	@GeneratedValue(strategy=GenerationType.IDENTITY)
//	@GeneratedValue(generator="projects_id_seq")
//	@SequenceGenerator(name = "projects_id_seq", sequenceName = "projects_id_seq")
	private Integer id;
	
	@Column(unique=true)
	private String name;

	public JProject() {
	}
	
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
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
