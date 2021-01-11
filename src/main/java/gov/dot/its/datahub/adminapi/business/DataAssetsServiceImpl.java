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

import gov.dot.its.datahub.adminapi.dao.DataAssetsDao;
import gov.dot.its.datahub.adminapi.model.ApiError;
import gov.dot.its.datahub.adminapi.model.ApiMessage;
import gov.dot.its.datahub.adminapi.model.ApiResponse;
import gov.dot.its.datahub.adminapi.model.DataAsset;
import gov.dot.its.datahub.adminapi.utils.ApiUtils;

@Service
public class DataAssetsServiceImpl implements DataAssetsService {

	private static final Logger loggerda = LoggerFactory.getLogger(ConfigurationServiceImpl.class);
	private static final String MESSAGE_TEMPLATE = "{} : {} ";
	static final String RESPONSE_MSG = "Response: GET Data Assets. ";

	@Autowired
	private DataAssetsDao dataAssetsDao;

	@Autowired
	private ApiUtils apiUtils;

	@Override
	public ApiResponse<List<DataAsset>> dataAssets(HttpServletRequest request) {
		loggerda.info("Request: Data Assets");

		ApiResponse<List<DataAsset>> apiResponse = new ApiResponse<>();
		List<ApiError> errors = new ArrayList<>();

		try {

			List<DataAsset> dataAssets = dataAssetsDao.getDataAssets();

			if (!dataAssets.isEmpty()) {
				apiResponse.setResponse(HttpStatus.OK, dataAssets, null, null, request);
				loggerda.info(MESSAGE_TEMPLATE, RESPONSE_MSG + " " + dataAssets.size());
				return apiResponse;
			}

			apiResponse.setResponse(HttpStatus.NO_CONTENT, null, null, null, request);
			loggerda.info(MESSAGE_TEMPLATE, RESPONSE_MSG);
			return apiResponse;


		} catch(ElasticsearchStatusException | IOException e) {
			return apiResponse.setResponse(HttpStatus.INTERNAL_SERVER_ERROR, null, null, apiUtils.getErrorsFromException(errors, e), request);
		}
	}

	@Override
	public ApiResponse<DataAsset> dataAsset(HttpServletRequest request, String id) {
		loggerda.info("Request: Get DataAsset by ID");
		final String RESPONSE_MSG = "Response: GET DataAsset ";

		List<ApiError> errors = new ArrayList<>();
		ApiResponse<DataAsset> apiResponse = new ApiResponse<>();
		List<ApiMessage> messages = new ArrayList<>();

		try {

			DataAsset dataAsset = dataAssetsDao.getDataAssetById(id);

			if (dataAsset != null) {
				apiResponse.setResponse(HttpStatus.OK, dataAsset, messages, null, request);
				loggerda.info(MESSAGE_TEMPLATE);
				return apiResponse;
			}

			apiResponse.setResponse(HttpStatus.NOT_FOUND, null, null, null, request);
			loggerda.info(MESSAGE_TEMPLATE);
			return apiResponse;


		} catch(ElasticsearchStatusException | IOException e) {
			return apiResponse.setResponse(HttpStatus.INTERNAL_SERVER_ERROR, null, null, apiUtils.getErrorsFromException(errors, e), request);
		}
	}

	@Override
	public ApiResponse<DataAsset> updateDataAsset(HttpServletRequest request, DataAsset dataAsset) {
		loggerda.info("Request: Update DataAsset");
		final String RESPONSE_MSG = "Response: PUT DataAsset ";

		List<ApiError> errors = new ArrayList<>();
		List<ApiMessage> messages = new ArrayList<>();
		ApiResponse<DataAsset> apiResponse = new ApiResponse<>();

		try {
			dataAsset.setDhLastUpdate(apiUtils.getCurrentUtcTimestamp());
			String result = dataAssetsDao.updateDataAsset(dataAsset);

			if (result != null) {
				messages.add(new ApiMessage(result));
				loggerda.info(MESSAGE_TEMPLATE + result);
				apiResponse.setResponse(HttpStatus.OK, dataAsset, messages, null, request);
				return apiResponse;
			}

			loggerda.info(MESSAGE_TEMPLATE, RESPONSE_MSG);
			apiResponse.setResponse(HttpStatus.INTERNAL_SERVER_ERROR, null, null, null, request);
			return apiResponse;


		} catch(ElasticsearchStatusException | IOException e) {
			return apiResponse.setResponse(HttpStatus.INTERNAL_SERVER_ERROR, null, null, apiUtils.getErrorsFromException(errors, e), request);
		}
	}

}
