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
			cascade={CascadeType.PERSIST, CascadeType.REMOVE})
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
	
	public Child addChild(String name) {
		Child child = new Child(name, this);
		children.add(child);
		return child;
	}
	
	public void removeChild(Child child) {
		children.removeIf(c -> c.getId().equals(child.getId()));
		child.setParent(null);
	}
	
}
