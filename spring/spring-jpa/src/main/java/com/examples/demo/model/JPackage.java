package com.examples.demo.model;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

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

	@NotNull
	@Column
	private String name;
	
	@NotNull
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="project_id")
	private JProject project;

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="parent_id")
	private JPackage parent;
	
	
	@OneToMany(mappedBy="parent", 
			   fetch=FetchType.LAZY,
			   orphanRemoval=true)
	private Set<JClass> classes = new HashSet<>();
	
	protected JPackage() {
	}
	
	public JPackage(String name, JProject project) {
		this(name, project, null);
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
	
	public JProject getProject() {
		return project;
	}

	public JPackage getParent() {
		return parent;
	}

	public Set<JClass> getClasses() {
		return classes;
	}

	void addClass(JClass clazz) {
		classes.add(clazz);
	}
}
