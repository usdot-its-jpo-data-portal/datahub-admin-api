package gov.dot.its.datahub.adminapi.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import gov.dot.its.datahub.adminapi.business.ConfigurationService;
import gov.dot.its.datahub.adminapi.model.ApiResponse;
import gov.dot.its.datahub.adminapi.model.DHConfiguration;
import gov.dot.its.datahub.adminapi.model.DHDataType;
import gov.dot.its.datahub.adminapi.model.DHProject;

@CrossOrigin(maxAge = 3600)
@RestController
public class ConfigurationController {

	@Autowired
	private ConfigurationService configurationService;

	@GetMapping(value="/v1/configurations", headers="Accept=application/json", produces="application/json")
	public ResponseEntity<ApiResponse<DHConfiguration>> configurations(HttpServletRequest request) {

		ApiResponse<DHConfiguration> apiResponse = configurationService.configurations(request);

		return new ResponseEntity<>(apiResponse, HttpStatus.OK);
	}

	@GetMapping(value="/v1/configurations/projects", headers="Accept=application/json", produces="application/json")
	public ResponseEntity<ApiResponse<List<DHProject>>> projects(HttpServletRequest request) {

		ApiResponse<List<DHProject>> apiResponse = configurationService.projects(request);

		return new ResponseEntity<>(apiResponse, HttpStatus.OK);
	}

	@GetMapping(value="/v1/configurations/projects/{id}", headers="Accept=application/json", produces="application/json")
	public ResponseEntity<ApiResponse<DHProject>> project(HttpServletRequest request, @PathVariable(name = "id") String id) {

		ApiResponse<DHProject> apiResponse = configurationService.project(request, id);

		return new ResponseEntity<>(apiResponse, HttpStatus.OK);
	}

	@PostMapping(value="/v1/configurations/projects", headers = "Accept=application/json", produces = "application/json")
	public ResponseEntity<ApiResponse<DHProject>> addProject(HttpServletRequest request, @RequestBody DHProject dhProject) {

		ApiResponse<DHProject> apiResponse = configurationService.addProject(request, dhProject);

		return new ResponseEntity<>(apiResponse, HttpStatus.OK);
	}

	@PutMapping(value="/v1/configurations/projects", headers="Accept=application/json", produces="application/json")
	public ResponseEntity<ApiResponse<DHProject>> updateProject(HttpServletRequest request, @RequestBody DHProject dhProject) {

		ApiResponse<DHProject> apiResponse = configurationService.updateProject(request, dhProject);

		return new ResponseEntity<>(apiResponse, HttpStatus.OK);
	}

	@DeleteMapping(value="/v1/configurations/projects/{id}", headers="Accept=application/json", produces="application/json")
	public ResponseEntity<ApiResponse<DHProject>> deleteProject(HttpServletRequest request, @PathVariable(name = "id") String id) {

		ApiResponse<DHProject> apiResponse = configurationService.deleteProject(request, id);

		return new ResponseEntity<>(apiResponse, HttpStatus.OK);
	}

	@GetMapping(value="/v1/images/projects", headers="Accept=application/json", produces="application/json")
	public ResponseEntity<ApiResponse<List<String>>> projectsImages(HttpServletRequest request) {

		ApiResponse<List<String>> apiResponse = configurationService.projectImages(request);

		return new ResponseEntity<>(apiResponse, HttpStatus.OK);
	}

	@GetMapping(value="/v1/configurations/datatypes", headers="Accept=application/json", produces="application/json")
	public ResponseEntity<ApiResponse<List<DHDataType>>> dataTypes(HttpServletRequest request) {

		ApiResponse<List<DHDataType>> apiResponse = configurationService.dataTypes(request);

		return new ResponseEntity<>(apiResponse, HttpStatus.OK);
	}

	@GetMapping(value="/v1/configurations/datatypes/{id}", headers="Accept=application/json", produces="application/json")
	public ResponseEntity<ApiResponse<DHDataType>> dataType(HttpServletRequest request, @PathVariable(name = "id") String id) {

		ApiResponse<DHDataType> apiResponse = configurationService.dataType(request, id);

		return new ResponseEntity<>(apiResponse, HttpStatus.OK);
	}

	@PostMapping(value="/v1/configurations/datatypes", headers = "Accept=application/json", produces = "application/json")
	public ResponseEntity<ApiResponse<DHDataType>> addDataType(HttpServletRequest request, @RequestBody DHDataType dhDataType) {

		ApiResponse<DHDataType> apiResponse = configurationService.addDataType(request, dhDataType);

		return new ResponseEntity<>(apiResponse, HttpStatus.OK);
	}

	@PutMapping(value="/v1/configurations/datatypes", headers="Accept=application/json", produces="application/json")
	public ResponseEntity<ApiResponse<DHDataType>> updateProject(HttpServletRequest request, @RequestBody DHDataType dhDataType) {

		ApiResponse<DHDataType> apiResponse = configurationService.updateDataType(request, dhDataType);

		return new ResponseEntity<>(apiResponse, HttpStatus.OK);
	}

	@DeleteMapping(value="/v1/configurations/datatypes/{id}", headers="Accept=application/json", produces="application/json")
	public ResponseEntity<ApiResponse<DHDataType>> deleteDataType(HttpServletRequest request, @PathVariable(name = "id") String id) {

		ApiResponse<DHDataType> apiResponse = configurationService.deleteDataType(request, id);

		return new ResponseEntity<>(apiResponse, HttpStatus.OK);
	}
}
