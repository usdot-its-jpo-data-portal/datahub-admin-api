package gov.dot.its.datahub.adminapi.dao;

import java.io.IOException;
import java.util.List;

import gov.dot.its.datahub.adminapi.model.DHConfiguration;
import gov.dot.its.datahub.adminapi.model.DHProject;

public interface ConfigurationDao {

	DHConfiguration getConfiguration() throws IOException;

	List<DHProject> getProjects() throws IOException;

	DHProject getProjectById(String id) throws IOException;

	String addProject(DHProject chProject) throws IOException;

	String updateProject(DHProject chProject) throws IOException;

	boolean deleteProjectById(String id) throws IOException;

	List<String> getProjectImages() throws IOException;

}
