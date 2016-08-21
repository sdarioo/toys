package com.examples.demo.model;

import javax.persistence.Entity;
import javax.persistence.Version;

@Entity
public class Customer extends EntityBase {

    private String name;

    @Version
    private Long version;
    
    protected Customer() {}

    public Customer(String name) {
        this.name = name;
    }
    
    public String getName() {
    	return name;
    }
    
    public void setName(String name) {
    	this.name = name;
    }
    
    @Override
    public String toString() {
        return String.format("Customer[id=%d, name='%s']", id, name);
    }

}