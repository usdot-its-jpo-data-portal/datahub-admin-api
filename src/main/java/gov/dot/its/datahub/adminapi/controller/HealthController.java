package gov.dot.its.datahub.adminapi.controller;

import java.util.Date;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import gov.dot.its.datahub.adminapi.model.ApiResponse;
import gov.dot.its.datahub.adminapi.utils.ApiUtils;

@CrossOrigin(maxAge = 3600)
@ComponentScan("gov.dot.its.datahub.adminapi.utils")
@RestController
public class HealthController {

	@Autowired
	private ApiUtils apiUtils;
	
	@GetMapping(value="/health", headers="Accept=application/json", produces="application/json")
	public ResponseEntity<ApiResponse<Date>> healthCheck(HttpServletRequest request) {

		ApiResponse<Date> apiResponse = new ApiResponse<>();
		apiResponse.setResponse(HttpStatus.OK, apiUtils.getCurrentUtc(), null, null, request);

		return new ResponseEntity<>(apiResponse, HttpStatus.OK);
	}
}
