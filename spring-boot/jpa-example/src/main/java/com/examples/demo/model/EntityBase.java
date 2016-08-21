package com.examples.demo.model;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;

@MappedSuperclass
public class EntityBase {

	@Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    protected Long id;
	
	
	public Long getId() {
		return id;
	}
}
