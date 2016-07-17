package com.examples.demo.model;

import javax.persistence.MappedSuperclass;

import org.springframework.data.jpa.domain.AbstractPersistable;

@MappedSuperclass
public abstract class JObject extends AbstractPersistable<Integer> {
	
	private static final long serialVersionUID = 1L;
	
	
	protected JObject() {}
	
}
