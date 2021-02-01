package gov.dot.its.datahub.adminapi.business;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import gov.dot.its.datahub.adminapi.model.ApiResponse;
import gov.dot.its.datahub.adminapi.model.DataAsset;

public interface DataAssetsService {

	ApiResponse<List<DataAsset>> dataAssets(HttpServletRequest request, boolean includeMasked);

	ApiResponse<DataAsset> dataAsset(HttpServletRequest request, String id);

	ApiResponse<DataAsset> updateDataAsset(HttpServletRequest request, DataAsset dataAsset);

}
