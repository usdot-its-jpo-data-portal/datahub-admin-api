package gov.dot.its.datahub.adminapi.dao;

import java.io.IOException;
import java.util.List;

import gov.dot.its.datahub.adminapi.model.DHConfiguration;
import gov.dot.its.datahub.adminapi.model.DHDataType;
import gov.dot.its.datahub.adminapi.model.DHProject;

public interface ConfigurationDao {

	DHConfiguration getConfiguration() throws IOException;

	List<DHProject> getProjects() throws IOException;

	DHProject getProjectById(String id) throws IOException;

	String addProject(DHProject chProject) throws IOException;

	String updateProject(DHProject chProject) throws IOException;

	boolean deleteProjectById(String id) throws IOException;

	List<String> getProjectImages() throws IOException;

	List<DHDataType> getDataTypes() throws IOException;

	DHDataType getDataTypeById(String id) throws IOException;

	String addDataType(DHDataType dhDataType) throws IOException;

	String updateDataType(DHDataType dhDataType) throws IOException;

	boolean deleteDataTypeById(String id) throws IOException;

}
