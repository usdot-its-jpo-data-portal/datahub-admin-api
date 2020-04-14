package gov.dot.its.datahub.adminapi.business;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.elasticsearch.ElasticsearchStatusException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import gov.dot.its.datahub.adminapi.dao.ConfigurationDao;
import gov.dot.its.datahub.adminapi.dao.DataAssetsDao;
import gov.dot.its.datahub.adminapi.model.ApiError;
import gov.dot.its.datahub.adminapi.model.ApiMessage;
import gov.dot.its.datahub.adminapi.model.ApiResponse;
import gov.dot.its.datahub.adminapi.model.DHConfiguration;
import gov.dot.its.datahub.adminapi.model.DHProject;
import gov.dot.its.datahub.adminapi.utils.ApiUtils;

@Service
public class ConfigurationServiceImpl implements ConfigurationService {

	private static final Logger logger = LoggerFactory.getLogger(ConfigurationServiceImpl.class);

	private static final String MESSAGE_TEMPLATE = "{} : {} ";

	@Autowired
	private ConfigurationDao configurationDao;

	@Autowired
	private DataAssetsDao dataAssetsDao;

	@Autowired
	private ApiUtils apiUtils;

	@Override
	public ApiResponse<DHConfiguration> configurations(HttpServletRequest request) {
		logger.info("Request: configurations");
		final String RESPONSE_MSG = "Response: GET Configurations. ";

		ApiResponse<DHConfiguration> apiResponse = new ApiResponse<>();
		List<ApiError> errors = new ArrayList<>();

		try {

			DHConfiguration configuration = configurationDao.getConfiguration();

			if (configuration != null) {
				apiResponse.setResponse(HttpStatus.OK, configuration, null, null, request);
				logger.info(MESSAGE_TEMPLATE, RESPONSE_MSG,HttpStatus.OK.toString());
				return apiResponse;
			}

			apiResponse.setResponse(HttpStatus.NO_CONTENT, null, null, null, request);
			logger.info(MESSAGE_TEMPLATE, RESPONSE_MSG, HttpStatus.NO_CONTENT.toString());
			return apiResponse;


		} catch(ElasticsearchStatusException | IOException e) {
			return apiResponse.setResponse(HttpStatus.INTERNAL_SERVER_ERROR, null, null, apiUtils.getErrorsFromException(errors, e), request);
		}
	}

	@Override
	public ApiResponse<List<DHProject>> projects(HttpServletRequest request) {
		logger.info("Request: Projects");
		final String RESPONSE_MSG = "Response: GET Projects. ";

		ApiResponse<List<DHProject>> apiResponse = new ApiResponse<>();
		List<ApiError> errors = new ArrayList<>();

		try {

			List<DHProject> projects = configurationDao.getProjects();

			if (projects != null && !projects.isEmpty()) {
				apiResponse.setResponse(HttpStatus.OK, projects, null, null, request);
				logger.info(MESSAGE_TEMPLATE, RESPONSE_MSG,HttpStatus.OK.toString()+" "+projects.size());
				return apiResponse;
			}

			apiResponse.setResponse(HttpStatus.NO_CONTENT, null, null, null, request);
			logger.info(MESSAGE_TEMPLATE, RESPONSE_MSG, HttpStatus.NO_CONTENT.toString());
			return apiResponse;


		} catch(ElasticsearchStatusException | IOException e) {
			return apiResponse.setResponse(HttpStatus.INTERNAL_SERVER_ERROR, null, null, apiUtils.getErrorsFromException(errors, e), request);
		}
	}

	@Override
	public ApiResponse<DHProject> project(HttpServletRequest request, String id) {
		logger.info("Request: Get Project by ID");
		final String RESPONSE_MSG = "Response: GET Project. ";

		List<ApiError> errors = new ArrayList<>();
		ApiResponse<DHProject> apiResponse = new ApiResponse<>();
		List<ApiMessage> messages = new ArrayList<>();

		try {

			DHProject project = configurationDao.getProjectById(id);

			if (project != null) {
				apiResponse.setResponse(HttpStatus.OK, project, messages, null, request);
				logger.info(MESSAGE_TEMPLATE, RESPONSE_MSG,HttpStatus.OK.toString());
				return apiResponse;
			}

			apiResponse.setResponse(HttpStatus.NOT_FOUND, null, null, null, request);
			logger.info(MESSAGE_TEMPLATE, RESPONSE_MSG, HttpStatus.NOT_FOUND.toString());
			return apiResponse;


		} catch(ElasticsearchStatusException | IOException e) {
			return apiResponse.setResponse(HttpStatus.INTERNAL_SERVER_ERROR, null, null, apiUtils.getErrorsFromException(errors, e), request);
		}
	}

	@Override
	public ApiResponse<DHProject> addProject(HttpServletRequest request, DHProject chProject) {
		logger.info("Request: Add Project");
		final String RESPONSE_MSG = "Response: POST Project. ";

		ApiResponse<DHProject> apiResponse = new ApiResponse<>();
		List<ApiError> errors = new ArrayList<>();
		List<ApiMessage> messages = new ArrayList<>();

		try {
			chProject.setId(apiUtils.getUUID());
			chProject.setLastModified(apiUtils.getCurrentUtc());
			String result = configurationDao.addProject(chProject);

			if (result != null) {
				messages.add(new ApiMessage(result));
				apiResponse.setResponse(HttpStatus.OK, chProject, messages, null, request);
				logger.info(MESSAGE_TEMPLATE, RESPONSE_MSG,HttpStatus.OK.toString()+" "+result);
				return apiResponse;
			}

			apiResponse.setResponse(HttpStatus.INTERNAL_SERVER_ERROR, null, null, null, request);
			logger.info(MESSAGE_TEMPLATE, RESPONSE_MSG, HttpStatus.INTERNAL_SERVER_ERROR.toString());
			return apiResponse;


		} catch(ElasticsearchStatusException | IOException e) {
			return apiResponse.setResponse(HttpStatus.INTERNAL_SERVER_ERROR, null, null, apiUtils.getErrorsFromException(errors, e), request);
		}
	}

	@Override
	public ApiResponse<DHProject> updateProject(HttpServletRequest request, DHProject chProject) {
		logger.info("Request: Update Project");
		final String RESPONSE_MSG = "Response: PUT Project. ";

		List<ApiError> errors = new ArrayList<>();
		List<ApiMessage> messages = new ArrayList<>();
		ApiResponse<DHProject> apiResponse = new ApiResponse<>();

		try {
			chProject.setLastModified(apiUtils.getCurrentUtc());
			String result = configurationDao.updateProject(chProject);

			if (result != null) {
				messages.add(new ApiMessage(result));
				logger.info(MESSAGE_TEMPLATE, RESPONSE_MSG,HttpStatus.OK.toString()+" "+result);
				apiResponse.setResponse(HttpStatus.OK, chProject, messages, null, request);
				return apiResponse;
			}

			logger.info(MESSAGE_TEMPLATE, RESPONSE_MSG, HttpStatus.INTERNAL_SERVER_ERROR.toString());
			apiResponse.setResponse(HttpStatus.INTERNAL_SERVER_ERROR, null, null, null, request);
			return apiResponse;


		} catch(ElasticsearchStatusException | IOException e) {
			return apiResponse.setResponse(HttpStatus.INTERNAL_SERVER_ERROR, null, null, apiUtils.getErrorsFromException(errors, e), request);
		}
	}

	@Override
	public ApiResponse<DHProject> deleteProject(HttpServletRequest request, String id) {
		logger.info("Request: DELETE Project");
		final String RESPONSE_MSG = "Response: DELETE Project. ";

		ApiResponse<DHProject> apiResponse = new ApiResponse<>();
		List<ApiMessage> messages = new ArrayList<>();
		List<ApiError> errors = new ArrayList<>();

		try {

			boolean result = configurationDao.deleteProjectById(id);

			if (result) {

				String msg = dataAssetsDao.removeProject(id);
				if (!StringUtils.isEmpty(msg)) {
					messages.add(new ApiMessage(msg));
				}

				messages.add(new ApiMessage(id));
				apiResponse.setResponse(HttpStatus.OK, null, messages, null, request);
				logger.info(MESSAGE_TEMPLATE, RESPONSE_MSG,HttpStatus.OK.toString());
				return apiResponse;
			}

			apiResponse.setResponse(HttpStatus.NOT_FOUND, null, null, null, request);
			logger.info(MESSAGE_TEMPLATE, RESPONSE_MSG, HttpStatus.NOT_FOUND.toString());
			return apiResponse;


		} catch(ElasticsearchStatusException | IOException e) {
			return apiResponse.setResponse(HttpStatus.INTERNAL_SERVER_ERROR, null, null, apiUtils.getErrorsFromException(errors, e), request);
		}
	}

	@Override
	public ApiResponse<List<String>> projectImages(HttpServletRequest request) {
		logger.info("Request: Project images");
		final String RESPONSE_MSG = "Response: GET Project images. ";

		ApiResponse<List<String>> apiResponse = new ApiResponse<>();
		List<ApiError> errors = new ArrayList<>();

		try {

			List<String> images = configurationDao.getProjectImages();

			if (images != null && !images.isEmpty()) {
				apiResponse.setResponse(HttpStatus.OK, images, null, null, request);
				logger.info(MESSAGE_TEMPLATE, RESPONSE_MSG,HttpStatus.OK.toString()+" "+images.size());
				return apiResponse;
			}

			apiResponse.setResponse(HttpStatus.NO_CONTENT, null, null, null, request);
			logger.info(MESSAGE_TEMPLATE, RESPONSE_MSG, HttpStatus.NO_CONTENT.toString());
			return apiResponse;


		} catch(ElasticsearchStatusException | IOException e) {
			return apiResponse.setResponse(HttpStatus.INTERNAL_SERVER_ERROR, null, null, apiUtils.getErrorsFromException(errors, e), request);
		}
	}

}
