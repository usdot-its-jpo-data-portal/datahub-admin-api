package gov.dot.its.datahub.adminapi.business;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import gov.dot.its.datahub.adminapi.model.ApiResponse;
import gov.dot.its.datahub.adminapi.model.DHConfiguration;
import gov.dot.its.datahub.adminapi.model.DHProject;

public interface ConfigurationService {

	ApiResponse<DHConfiguration> configurations(HttpServletRequest request);

	ApiResponse<List<DHProject>> projects(HttpServletRequest request);

	ApiResponse<DHProject> project(HttpServletRequest request, String id);

	ApiResponse<DHProject> addProject(HttpServletRequest request, DHProject chProject);

	ApiResponse<DHProject> updateProject(HttpServletRequest request, DHProject chProject);

	ApiResponse<DHProject> deleteProject(HttpServletRequest request, String id);

	ApiResponse<List<String>> projectImages(HttpServletRequest request);

}
