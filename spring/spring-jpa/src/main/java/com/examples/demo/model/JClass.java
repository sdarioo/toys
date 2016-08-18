package com.examples.demo.model;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

@Entity
@Table(name="classes")
public class JClass extends JObject {

	private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue(generator="classes_id_seq")
	@SequenceGenerator(name = "classes_id_seq", sequenceName = "classes_id_seq", allocationSize=1)
	private Integer id;
	
	@NotNull
	private String name;

	@NotNull
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="package_id")
	private JPackage parent;
	
	protected JClass() { }
	
	public JClass(String name, JPackage parent) {
		this.name = name;
		this.parent = parent;
		this.parent.addClass(this);
	}
	
	@Override
	public Integer getId() {
		return id;
	}
	
	public JPackage getPackage() {
		return parent;
	}

}
