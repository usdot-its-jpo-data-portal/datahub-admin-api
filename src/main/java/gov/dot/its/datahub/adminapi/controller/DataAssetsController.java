package gov.dot.its.datahub.adminapi.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import gov.dot.its.datahub.adminapi.business.DataAssetsService;
import gov.dot.its.datahub.adminapi.model.ApiResponse;
import gov.dot.its.datahub.adminapi.model.DataAsset;

@CrossOrigin(maxAge = 3600)
@RestController
public class DataAssetsController {

	@Autowired
	private DataAssetsService dataAssetsService;

	@GetMapping(value="/v1/dataassets", headers="Accept=application/json", produces="application/json")
	public ResponseEntity<ApiResponse<List<DataAsset>>> dataassets(
		HttpServletRequest request, 
		@RequestParam(value="includeMasked", required=false, defaultValue="false") String includeMaskedString) {

		boolean includeMasked = includeMaskedString == "false" ? false : true;
		ApiResponse<List<DataAsset>> apiResponse = dataAssetsService.dataAssets(request, includeMasked);

		return new ResponseEntity<>(apiResponse, HttpStatus.OK);
	}

	@GetMapping(value="/v1/dataassets/{id}", headers="Accept=application/json", produces="application/json")
	public ResponseEntity<ApiResponse<DataAsset>> dataasset(HttpServletRequest request, @PathVariable(name="id") String id) {

		ApiResponse<DataAsset> apiResponse = dataAssetsService.dataAsset(request, id);

		return new ResponseEntity<>(apiResponse, HttpStatus.OK);
	}

	@PutMapping(value="/v1/dataassets", headers="Accept=application/json", produces="application/json")
	public ResponseEntity<ApiResponse<DataAsset>> updateDataAsset(HttpServletRequest request, @RequestBody DataAsset dataAsset) {

		ApiResponse<DataAsset> apiResponse = dataAssetsService.updateDataAsset(request, dataAsset);

		return new ResponseEntity<>(apiResponse, HttpStatus.OK);
	}
}
