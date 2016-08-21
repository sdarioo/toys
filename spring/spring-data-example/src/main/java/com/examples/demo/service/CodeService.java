package com.examples.demo.service;

import java.util.List;

import com.examples.demo.model.JPackage;
import com.examples.demo.model.JProject;

public interface CodeService {
	
	JProject addProject(String name);
	
	List<JProject> getProjects();
	
	JPackage addPackage(String name, JProject project, JPackage parent);
	
	List<JPackage> getPackages();
	
	List<JPackage> getRootPackages(JProject project);
	
	List<JPackage> getChildPackages(JPackage parent);
	
}
