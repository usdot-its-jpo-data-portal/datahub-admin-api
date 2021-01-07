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
import gov.dot.its.datahub.adminapi.model.DHDataType;
import gov.dot.its.datahub.adminapi.model.DHEngagementPopup;
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
				logger.info(MESSAGE_TEMPLATE, RESPONSE_MSG);
				return apiResponse;
			}

			apiResponse.setResponse(HttpStatus.NO_CONTENT, null, null, null, request);
			logger.info(MESSAGE_TEMPLATE, RESPONSE_MSG);
			return apiResponse;

		} catch (ElasticsearchStatusException | IOException e) {
			return apiResponse.setResponse(HttpStatus.INTERNAL_SERVER_ERROR, null, null,
					apiUtils.getErrorsFromException(errors, e), request);
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
				logger.info(MESSAGE_TEMPLATE, RESPONSE_MSG + " " + projects.size());
				return apiResponse;
			}

			apiResponse.setResponse(HttpStatus.NO_CONTENT, null, null, null, request);
			logger.info(MESSAGE_TEMPLATE, RESPONSE_MSG, HttpStatus.NO_CONTENT.toString());
			return apiResponse;

		} catch (ElasticsearchStatusException | IOException e) {
			return apiResponse.setResponse(HttpStatus.INTERNAL_SERVER_ERROR, null, null,
					apiUtils.getErrorsFromException(errors, e), request);
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
				logger.info(MESSAGE_TEMPLATE, RESPONSE_MSG);
				return apiResponse;
			}

			apiResponse.setResponse(HttpStatus.NOT_FOUND, null, null, null, request);
			logger.info(MESSAGE_TEMPLATE, RESPONSE_MSG);
			return apiResponse;

		} catch (ElasticsearchStatusException | IOException e) {
			return apiResponse.setResponse(HttpStatus.INTERNAL_SERVER_ERROR, null, null,
					apiUtils.getErrorsFromException(errors, e), request);
		}
	}

	@Override
	public ApiResponse<DHProject> addProject(HttpServletRequest request, DHProject project) {
		logger.info("Request: Add Project");
		final String RESPONSE_MSG = "Response: POST Project. ";

		ApiResponse<DHProject> apiResponse = new ApiResponse<>();
		List<ApiError> errors = new ArrayList<>();
		List<ApiMessage> messages = new ArrayList<>();

		try {
			project.setId(apiUtils.getUUID());
			project.setLastModified(apiUtils.getCurrentUtc());
			String result = configurationDao.addProject(project);

			if (result != null) {
				messages.add(new ApiMessage(result));
				apiResponse.setResponse(HttpStatus.OK, project, messages, null, request);
				logger.info(MESSAGE_TEMPLATE, RESPONSE_MSG + " " + result);
				return apiResponse;
			}

			apiResponse.setResponse(HttpStatus.INTERNAL_SERVER_ERROR, null, null, null, request);
			logger.info(MESSAGE_TEMPLATE, RESPONSE_MSG);
			return apiResponse;

		} catch (ElasticsearchStatusException | IOException e) {
			return apiResponse.setResponse(HttpStatus.INTERNAL_SERVER_ERROR, null, null,
					apiUtils.getErrorsFromException(errors, e), request);
		}
	}

	@Override
	public ApiResponse<DHProject> updateProject(HttpServletRequest request, DHProject project) {
		logger.info("Request: Update Project");
		final String RESPONSE_MSG = "Response: PUT Project. ";

		List<ApiError> errors = new ArrayList<>();
		List<ApiMessage> messages = new ArrayList<>();
		ApiResponse<DHProject> apiResponse = new ApiResponse<>();

		try {
			project.setLastModified(apiUtils.getCurrentUtc());
			String result = configurationDao.updateProject(project);

			if (result != null) {
				messages.add(new ApiMessage(result));
				logger.info(MESSAGE_TEMPLATE, RESPONSE_MSG + " " + result);
				apiResponse.setResponse(HttpStatus.OK, project, messages, null, request);
				return apiResponse;
			}

			logger.info(MESSAGE_TEMPLATE, RESPONSE_MSG);
			apiResponse.setResponse(HttpStatus.INTERNAL_SERVER_ERROR, null, null, null, request);
			return apiResponse;

		} catch (ElasticsearchStatusException | IOException e) {
			return apiResponse.setResponse(HttpStatus.INTERNAL_SERVER_ERROR, null, null,
					apiUtils.getErrorsFromException(errors, e), request);
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
				logger.info(MESSAGE_TEMPLATE, RESPONSE_MSG);
				return apiResponse;
			}

			apiResponse.setResponse(HttpStatus.NOT_FOUND, null, null, null, request);
			logger.info(MESSAGE_TEMPLATE, RESPONSE_MSG);
			return apiResponse;

		} catch (ElasticsearchStatusException | IOException e) {
			return apiResponse.setResponse(HttpStatus.INTERNAL_SERVER_ERROR, null, null,
					apiUtils.getErrorsFromException(errors, e), request);
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
				logger.info(MESSAGE_TEMPLATE, RESPONSE_MSG + " " + images.size());
				return apiResponse;
			}

			apiResponse.setResponse(HttpStatus.NO_CONTENT, null, null, null, request);
			logger.info(MESSAGE_TEMPLATE, RESPONSE_MSG);
			return apiResponse;

		} catch (ElasticsearchStatusException | IOException e) {
			return apiResponse.setResponse(HttpStatus.INTERNAL_SERVER_ERROR, null, null,
					apiUtils.getErrorsFromException(errors, e), request);
		}
	}

	@Override
	public ApiResponse<List<DHDataType>> dataTypes(HttpServletRequest request) {
		logger.info("Request: DataTypes");
		final String RESPONSE_MSG = "Response: GET DataTypes. ";

		ApiResponse<List<DHDataType>> apiResponse = new ApiResponse<>();
		List<ApiError> errors = new ArrayList<>();

		try {

			List<DHDataType> dataTypes = configurationDao.getDataTypes();

			if (dataTypes != null && !dataTypes.isEmpty()) {
				apiResponse.setResponse(HttpStatus.OK, dataTypes, null, null, request);
				logger.info(MESSAGE_TEMPLATE, RESPONSE_MSG + " " + dataTypes.size());
				return apiResponse;
			}

			apiResponse.setResponse(HttpStatus.NO_CONTENT, null, null, null, request);
			logger.info(MESSAGE_TEMPLATE, RESPONSE_MSG);
			return apiResponse;

		} catch (ElasticsearchStatusException | IOException e) {
			return apiResponse.setResponse(HttpStatus.INTERNAL_SERVER_ERROR, null, null,
					apiUtils.getErrorsFromException(errors, e), request);
		}
	}

	@Override
	public ApiResponse<DHDataType> dataType(HttpServletRequest request, String id) {
		logger.info("Request: Get DataType by ID");
		final String RESPONSE_MSG = "Response: GET DataType. ";

		List<ApiError> errors = new ArrayList<>();
		ApiResponse<DHDataType> apiResponse = new ApiResponse<>();
		List<ApiMessage> messages = new ArrayList<>();

		try {

			DHDataType dataType = configurationDao.getDataTypeById(id);

			if (dataType != null) {
				apiResponse.setResponse(HttpStatus.OK, dataType, messages, null, request);
				logger.info(MESSAGE_TEMPLATE, RESPONSE_MSG);
				return apiResponse;
			}

			apiResponse.setResponse(HttpStatus.NOT_FOUND, null, null, null, request);
			logger.info(MESSAGE_TEMPLATE, RESPONSE_MSG);
			return apiResponse;

		} catch (ElasticsearchStatusException | IOException e) {
			return apiResponse.setResponse(HttpStatus.INTERNAL_SERVER_ERROR, null, null,
					apiUtils.getErrorsFromException(errors, e), request);
		}
	}

	@Override
	public ApiResponse<DHDataType> addDataType(HttpServletRequest request, DHDataType dhDataType) {
		logger.info("Request: Add DataType");
		final String RESPONSE_MSG = "Response: POST DataType. ";

		ApiResponse<DHDataType> apiResponse = new ApiResponse<>();
		List<ApiError> errors = new ArrayList<>();
		List<ApiMessage> messages = new ArrayList<>();

		try {
			dhDataType.setId(apiUtils.getUUID());
			dhDataType.setLastModified(apiUtils.getCurrentUtc());
			String result = configurationDao.addDataType(dhDataType);

			if (result != null) {
				messages.add(new ApiMessage(result));
				apiResponse.setResponse(HttpStatus.OK, dhDataType, messages, null, request);
				logger.info(MESSAGE_TEMPLATE, RESPONSE_MSG + " " + result);
				return apiResponse;
			}

			apiResponse.setResponse(HttpStatus.INTERNAL_SERVER_ERROR, null, null, null, request);
			logger.info(MESSAGE_TEMPLATE, RESPONSE_MSG);
			return apiResponse;

		} catch (ElasticsearchStatusException | IOException e) {
			return apiResponse.setResponse(HttpStatus.INTERNAL_SERVER_ERROR, null, null,
					apiUtils.getErrorsFromException(errors, e), request);
		}
	}

	@Override
	public ApiResponse<DHDataType> updateDataType(HttpServletRequest request, DHDataType dhDataType) {
		logger.info("Request: Update DataType");
		final String RESPONSE_MSG = "Response: PUT DataType. ";

		List<ApiError> errors = new ArrayList<>();
		List<ApiMessage> messages = new ArrayList<>();
		ApiResponse<DHDataType> apiResponse = new ApiResponse<>();

		try {
			dhDataType.setLastModified(apiUtils.getCurrentUtc());
			String result = configurationDao.updateDataType(dhDataType);

			if (result != null) {
				messages.add(new ApiMessage(result));
				logger.info(MESSAGE_TEMPLATE, RESPONSE_MSG + " " + result);
				apiResponse.setResponse(HttpStatus.OK, dhDataType, messages, null, request);
				return apiResponse;
			}

			logger.info(MESSAGE_TEMPLATE, RESPONSE_MSG);
			apiResponse.setResponse(HttpStatus.INTERNAL_SERVER_ERROR, null, null, null, request);
			return apiResponse;

		} catch (ElasticsearchStatusException | IOException e) {
			return apiResponse.setResponse(HttpStatus.INTERNAL_SERVER_ERROR, null, null,
					apiUtils.getErrorsFromException(errors, e), request);
		}
	}

	@Override
	public ApiResponse<DHDataType> deleteDataType(HttpServletRequest request, String id) {
		logger.info("Request: DELETE DataType");
		final String RESPONSE_MSG = "Response: DELETE DataType. ";

		ApiResponse<DHDataType> apiResponse = new ApiResponse<>();
		List<ApiMessage> messages = new ArrayList<>();
		List<ApiError> errors = new ArrayList<>();

		try {

			boolean result = configurationDao.deleteDataTypeById(id);

			if (result) {

				String msg = dataAssetsDao.removeDataType(id);
				if (!StringUtils.isEmpty(msg)) {
					messages.add(new ApiMessage(msg));
				}

				logger.info(MESSAGE_TEMPLATE, RESPONSE_MSG);
				messages.add(new ApiMessage(id));
				apiResponse.setResponse(HttpStatus.OK, null, messages, null, request);
				return apiResponse;
			}

			apiResponse.setResponse(HttpStatus.NOT_FOUND, null, null, null, request);
			logger.info(MESSAGE_TEMPLATE, RESPONSE_MSG);
			return apiResponse;

		} catch (ElasticsearchStatusException | IOException e) {
			return apiResponse.setResponse(HttpStatus.INTERNAL_SERVER_ERROR, null, null,
					apiUtils.getErrorsFromException(errors, e), request);
		}
	}

	@Override
	public ApiResponse<List<DHEngagementPopup>> engagementpopups(HttpServletRequest request) {
		logger.info("Request: Engagement Popups");
		final String RESPONSE_MSG = "Response: GET Engagement Popups. ";

		ApiResponse<List<DHEngagementPopup>> apiResponse = new ApiResponse<>();
		List<ApiError> errors = new ArrayList<>();

		try {

			List<DHEngagementPopup> engagementPopups = configurationDao.getEngagementPopups();

			if (engagementPopups != null && !engagementPopups.isEmpty()) {
				apiResponse.setResponse(HttpStatus.OK, engagementPopups, null, null, request);
				logger.info(MESSAGE_TEMPLATE, RESPONSE_MSG + " " + engagementPopups.size());
				return apiResponse;
			}

			apiResponse.setResponse(HttpStatus.NO_CONTENT, null, null, null, request);
			logger.info(MESSAGE_TEMPLATE, RESPONSE_MSG);
			return apiResponse;


		} catch(ElasticsearchStatusException | IOException e) {
			return apiResponse.setResponse(HttpStatus.INTERNAL_SERVER_ERROR, null, null, apiUtils.getErrorsFromException(errors, e), request);
		}
	}

	@Override
	public ApiResponse<DHEngagementPopup> addEngagementPopup(HttpServletRequest request, DHEngagementPopup cdEngagementPopup) {
		logger.info("Request: POST Engagement Popup");
		final String RESPONSE_MSG = "Response: POST Engagement Popup. ";

		List<ApiError> errors = new ArrayList<>();
		List<ApiMessage> messages = new ArrayList<>();
		ApiResponse<DHEngagementPopup> apiResponse = new ApiResponse<>();

		try {
			cdEngagementPopup.setId(apiUtils.getUUID());
			cdEngagementPopup.setLastModified(apiUtils.getCurrentUtc());
			String result = configurationDao.addEngagementPopup(cdEngagementPopup);

			if (result != null) {
				messages.add(new ApiMessage(result));
				logger.info(MESSAGE_TEMPLATE + " " + result);
				apiResponse.setResponse(HttpStatus.OK, cdEngagementPopup, messages, null, request);
				return apiResponse;
			}

			logger.info(MESSAGE_TEMPLATE, RESPONSE_MSG);
			apiResponse.setResponse(HttpStatus.INTERNAL_SERVER_ERROR, null, null, null, request);
			return apiResponse;

		} catch(ElasticsearchStatusException | IOException e) {
			return apiResponse.setResponse(HttpStatus.INTERNAL_SERVER_ERROR, null, null, apiUtils.getErrorsFromException(errors, e), request);
		}
	}

	@Override
	public ApiResponse<DHEngagementPopup> updateEngagementPopup(HttpServletRequest request, DHEngagementPopup engagementPopup) {
		logger.info("Request: Update Engagement Popup");
		final String RESPONSE_MSG = "Response: PUT Engagement Popup. ";

		List<ApiError> errors = new ArrayList<>();
		List<ApiMessage> messages = new ArrayList<>();
		ApiResponse<DHEngagementPopup> apiResponse = new ApiResponse<>();

		try {
			engagementPopup.setLastModified(apiUtils.getCurrentUtc());
			String result = configurationDao.updateEngagementPopup(engagementPopup);

			if (result != null) {
				messages.add(new ApiMessage(result));
				logger.info(MESSAGE_TEMPLATE, RESPONSE_MSG + " " + result);
				apiResponse.setResponse(HttpStatus.OK, engagementPopup, messages, null, request);
				return apiResponse;
			}

			logger.info(MESSAGE_TEMPLATE, RESPONSE_MSG);
			apiResponse.setResponse(HttpStatus.INTERNAL_SERVER_ERROR, null, null, null, request);
			return apiResponse;

		} catch(ElasticsearchStatusException | IOException e) {
			return apiResponse.setResponse(HttpStatus.INTERNAL_SERVER_ERROR, null, null, apiUtils.getErrorsFromException(errors, e), request);
		}
	}

	@Override
	public ApiResponse<DHEngagementPopup> deleteEngagementPopup(HttpServletRequest request, String id) {
		logger.info("Request: DELETE Engagement PopUp");
		final String RESPONSE_MSG = "Response: DELETE Engagement Popup. ";

		ApiResponse<DHEngagementPopup> apiResponse = new ApiResponse<>();
		List<ApiMessage> messages = new ArrayList<>();
		List<ApiError> errors = new ArrayList<>();

		try {

			boolean result = configurationDao.removeEngagementPopup(id);

			if (result) {
				messages.add(new ApiMessage(id));
				apiResponse.setResponse(HttpStatus.OK, null, messages, null, request);
				logger.info(MESSAGE_TEMPLATE);
				return apiResponse;
			}

			apiResponse.setResponse(HttpStatus.NOT_FOUND, null, null, null, request);
			logger.info(MESSAGE_TEMPLATE, RESPONSE_MSG);
			return apiResponse;

		} catch(ElasticsearchStatusException | IOException e) {
			return apiResponse.setResponse(HttpStatus.INTERNAL_SERVER_ERROR, null, null, apiUtils.getErrorsFromException(errors, e), request);
		}
	}

}
