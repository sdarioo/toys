package com.examples.demo.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.examples.demo.model.JPackage;
import com.examples.demo.model.JProject;
import com.examples.demo.repository.PackageRepository;
import com.examples.demo.repository.ProjectRepository;

@Service
public class CodeServiceImpl implements CodeService {

	private final ProjectRepository projectRepository;
	private final PackageRepository packageRepository;
	
	@Autowired
	public CodeServiceImpl(ProjectRepository projectRepository, PackageRepository packageRepository)
	{
		this.projectRepository = projectRepository;
		this.packageRepository = packageRepository;
	}
	
	@Override
	@Transactional
	public JProject addProject(String name) {
		JProject project = new JProject(name);
		return projectRepository.save(project);
	}

	@Override
	@Transactional(readOnly=true)
	public List<JProject> getProjects() {
		return projectRepository.findAll();
	}

	@Override
	@Transactional
	public JPackage addPackage(String name, JProject project, JPackage parent) {
		JPackage pkg = new JPackage(name, project, parent);
		return packageRepository.save(pkg);
	}

	@Override
	@Transactional(readOnly=true)
	public List<JPackage> getPackages() {
		return packageRepository.findAll();
	}

	@Override
	@Transactional(readOnly=true)
	public List<JPackage> getRootPackages(JProject project) {
		return packageRepository.findRootPackages(project);
	}

	@Override
	public List<JPackage> getChildPackages(JPackage parent) {
		return packageRepository.findChildPackages(parent);
	}
}
