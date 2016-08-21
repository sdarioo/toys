package com.examples.demo.model;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@Entity
public class Child extends EntityBase {
	
	private String name;
	
	@ManyToOne
	@JoinColumn(name="parent_id")
	private Parent parent;
	
	protected Child() {}
	
	public Child(String name, Parent parent) {
		this.name = name;
	}
	
	public String getName() {
		return name;
	}
	
	public Parent getParent() {
		return parent;
	}
}
