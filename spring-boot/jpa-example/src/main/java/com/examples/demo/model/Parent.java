package com.examples.demo.model;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;

@Entity
public class Parent extends EntityBase {
	
	private String name;

	@OneToMany(mappedBy="parent", 
			fetch=FetchType.LAZY,
			orphanRemoval=true, 
			cascade=CascadeType.ALL)
	private Set<Child> children = new HashSet<>();
	
	protected Parent() {}
	
	public Parent(String name) {
		this.name = name;
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	public Set<Child> getChildren() {
		return children;
	}
	
	public void addChild(String name) {
		children.add(new Child(name, this));
	}
	
}
