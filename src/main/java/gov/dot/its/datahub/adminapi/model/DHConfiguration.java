package gov.dot.its.datahub.adminapi.model;

import java.util.ArrayList;
import java.util.List;

public class DHConfiguration {
	private String id;
	private String name;
	private List<DHProject> projects;

	public DHConfiguration() {
		this.projects = new ArrayList<>();
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<DHProject> getProjects() {
		return projects;
	}

	public void setProjects(List<DHProject> projects) {
		this.projects = projects;
	}


}
