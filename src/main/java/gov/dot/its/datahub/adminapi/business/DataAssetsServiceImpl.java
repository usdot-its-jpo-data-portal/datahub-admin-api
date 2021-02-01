package gov.dot.its.datahub.adminapi.business;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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

	private static final Logger logger = LoggerFactory.getLogger(ConfigurationServiceImpl.class);
	private static final String MESSAGE_TEMPLATE = "{} : {} ";

	@Autowired
	private DataAssetsDao dataAssetsDao;

	@Autowired
	private ApiUtils apiUtils;

	@Override
	public ApiResponse<List<DataAsset>> dataAssets(HttpServletRequest request, boolean includeMasked) {
		logger.info("Request: Data Assets");
		final String RESPONSE_MSG = "Response: GET Data Assets. ";

		ApiResponse<List<DataAsset>> apiResponse = new ApiResponse<>();
		List<ApiError> errors = new ArrayList<>();

		try {
			List<DataAsset> dataAssets = dataAssetsDao.getDataAssets();
			if (!includeMasked) {
				dataAssets = dataAssets.stream().filter(d -> !d.isHidden()).collect(Collectors.toList());
			}

			if (!dataAssets.isEmpty()) {
				apiResponse.setResponse(HttpStatus.OK, dataAssets, null, null, request);
				logger.info(MESSAGE_TEMPLATE, RESPONSE_MSG,HttpStatus.OK.toString()+" "+dataAssets.size());
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
	public ApiResponse<DataAsset> dataAsset(HttpServletRequest request, String id) {
		logger.info("Request: Get DataAsset by ID");
		final String RESPONSE_MSG = "Response: GET DataAsset ";

		List<ApiError> errors = new ArrayList<>();
		ApiResponse<DataAsset> apiResponse = new ApiResponse<>();
		List<ApiMessage> messages = new ArrayList<>();

		try {

			DataAsset dataAsset = dataAssetsDao.getDataAssetById(id);

			if (dataAsset != null) {
				apiResponse.setResponse(HttpStatus.OK, dataAsset, messages, null, request);
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
	public ApiResponse<DataAsset> updateDataAsset(HttpServletRequest request, DataAsset dataAsset) {
		logger.info("Request: Update DataAsset");
		final String RESPONSE_MSG = "Response: PUT DataAsset ";

		List<ApiError> errors = new ArrayList<>();
		List<ApiMessage> messages = new ArrayList<>();
		ApiResponse<DataAsset> apiResponse = new ApiResponse<>();

		try {
			dataAsset.setDhLastUpdate(apiUtils.getCurrentUtcTimestamp());
			String result = dataAssetsDao.updateDataAsset(dataAsset);

			if (result != null) {
				messages.add(new ApiMessage(result));
				logger.info(MESSAGE_TEMPLATE, RESPONSE_MSG,HttpStatus.OK.toString()+" "+result);
				apiResponse.setResponse(HttpStatus.OK, dataAsset, messages, null, request);
				return apiResponse;
			}

			logger.info(MESSAGE_TEMPLATE, RESPONSE_MSG, HttpStatus.INTERNAL_SERVER_ERROR.toString());
			apiResponse.setResponse(HttpStatus.INTERNAL_SERVER_ERROR, null, null, null, request);
			return apiResponse;


		} catch(ElasticsearchStatusException | IOException e) {
			return apiResponse.setResponse(HttpStatus.INTERNAL_SERVER_ERROR, null, null, apiUtils.getErrorsFromException(errors, e), request);
		}
	}

}
