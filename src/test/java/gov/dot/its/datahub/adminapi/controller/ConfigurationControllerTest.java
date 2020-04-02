package gov.dot.its.datahub.adminapi.controller;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;

import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.restdocs.operation.preprocess.Preprocessors;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import gov.dot.its.datahub.adminapi.business.ConfigurationService;
import gov.dot.its.datahub.adminapi.model.ApiResponse;
import gov.dot.its.datahub.adminapi.model.DHConfiguration;
import gov.dot.its.datahub.adminapi.model.DHProject;
import gov.dot.its.datahub.adminapi.testutils.TestUtils;

@RunWith(SpringRunner.class)
@WebMvcTest(ConfigurationController.class)
@AutoConfigureRestDocs(outputDir = "target/generated-snippets", uriHost = "example.com", uriPort = 3008, uriScheme = "http")
@ComponentScan("gov.dot.its.datahub.adminapi.testutils")
class ConfigurationControllerTest {

	private static final String URL_PROJECTS_TEMPLATE = "%s/v1/configurations/projects";
	private static final String SECURITY_TOKEN_KEY = "datahub.admin.api.security.token.key";

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private TestUtils testUtils;

	@Autowired
	private ObjectMapper objectMapper;

	@Autowired
	private Environment env;

	@MockBean
	private ConfigurationService configurationService;

	static {
		System.setProperty(SECURITY_TOKEN_KEY, "123");
	}

	@Test
	void testUnAuthorized() throws Exception { // NOSONAR
		final String SERVER_SERVLET_CONTEXT_PATH = "server.servlet.context-path";
		final String HEADER_HOST = "Host";
		final String HEADER_CONTENT_LENGTH = "Content-Length";
		MockHttpServletRequest request = new MockHttpServletRequest();
		request.setMethod("GET");

		DHConfiguration configuration = this.getFakeConfiguration();

		ApiResponse<DHConfiguration> apiResponse = new ApiResponse<>();
		apiResponse.setResponse(HttpStatus.OK, configuration, null, null, request);

		when(configurationService.configurations(any(HttpServletRequest.class))).thenReturn(apiResponse);
		ResultActions resultActions = mockMvc.perform(get(String.format("%s/v1/configurations", env.getProperty(SERVER_SERVLET_CONTEXT_PATH)))
				.contextPath(String.format("%s", env.getProperty(SERVER_SERVLET_CONTEXT_PATH)))).andExpect(status().is4xxClientError())
				.andDo(document("api/auth",
						Preprocessors.preprocessRequest(Preprocessors.prettyPrint(),
								Preprocessors.removeHeaders(HEADER_HOST, HEADER_CONTENT_LENGTH)),
						Preprocessors.preprocessResponse(Preprocessors.prettyPrint(),
								Preprocessors.removeHeaders(HEADER_HOST, HEADER_CONTENT_LENGTH)))); 

		MvcResult result = resultActions.andReturn();

		assertEquals(HttpStatus.FORBIDDEN.value(), result.getResponse().getStatus());
		assertTrue(result.getResponse().getErrorMessage().equalsIgnoreCase("Access Denied"));
	}

	@Test
	void testConfigurations() throws Exception { // NOSONAR
		MockHttpServletRequest request = new MockHttpServletRequest();
		request.setMethod("GET");

		DHConfiguration configuration = this.getFakeConfiguration();

		ApiResponse<DHConfiguration> apiResponse = new ApiResponse<>();
		apiResponse.setResponse(HttpStatus.OK, configuration, null, null, request);

		when(configurationService.configurations(any(HttpServletRequest.class))).thenReturn(apiResponse);
		ResultActions resultActions = this.testUtils.prepareResultActions(this.mockMvc, request.getMethod(),
				"%s/v1/configurations", "api/v1/configurations/data", "");

		MvcResult result = resultActions.andReturn();
		String objString = result.getResponse().getContentAsString();

		TypeReference<ApiResponse<DHConfiguration>> valueType = new TypeReference<ApiResponse<DHConfiguration>>() {
		};
		ApiResponse<DHConfiguration> responseApi = objectMapper.readValue(objString, valueType);

		assertTrue(responseApi.getErrors() == null);
		assertTrue(responseApi.getResult() != null);
		assertEquals(HttpStatus.OK.value(), responseApi.getCode());
		assertTrue(responseApi.getMessages() == null);
	}

	@Test
	void testProjects() throws Exception { // NOSONAR
		MockHttpServletRequest request = new MockHttpServletRequest();
		request.setMethod("GET");

		DHConfiguration configuration = this.getFakeConfiguration();

		ApiResponse<List<DHProject>> apiResponse = new ApiResponse<>();
		apiResponse.setResponse(HttpStatus.OK, configuration.getProjects(), null, null, request);

		when(configurationService.projects(any(HttpServletRequest.class))).thenReturn(apiResponse);
		ResultActions resultActions = this.testUtils.prepareResultActions(this.mockMvc, request.getMethod(),
				URL_PROJECTS_TEMPLATE, "api/v1/configurations/projects/get", null);

		MvcResult result = resultActions.andReturn();
		String objString = result.getResponse().getContentAsString();

		TypeReference<ApiResponse<List<DHProject>>> valueType = new TypeReference<ApiResponse<List<DHProject>>>() {
		};
		ApiResponse<List<DHProject>> responseApi = objectMapper.readValue(objString, valueType);

		assertEquals(HttpStatus.OK.value(), responseApi.getCode());
		assertTrue(responseApi.getErrors() == null);
		assertTrue(!responseApi.getResult().isEmpty());
		assertTrue(responseApi.getMessages() == null);
	}

	@Test
	void testProject() throws Exception { // NOSONAR
		MockHttpServletRequest request = new MockHttpServletRequest();
		request.setMethod("GET");

		DHProject project = this.getFakeProject(1);

		ApiResponse<DHProject> apiResponse = new ApiResponse<>();
		apiResponse.setResponse(HttpStatus.OK, project, null, null, request);

		when(configurationService.project(any(HttpServletRequest.class), any(String.class))).thenReturn(apiResponse);
		ResultActions resultActions = this.testUtils.prepareResultActions(this.mockMvc, request.getMethod(),
				URL_PROJECTS_TEMPLATE + "/" + project.getId(), "api/v1/configurations/projects/get-id", null);

		MvcResult result = resultActions.andReturn();
		String objString = result.getResponse().getContentAsString();

		TypeReference<ApiResponse<DHProject>> valueType = new TypeReference<ApiResponse<DHProject>>() {
		};
		ApiResponse<DHProject> responseApi = objectMapper.readValue(objString, valueType);

		assertTrue(responseApi.getErrors() == null);
		assertTrue(responseApi.getMessages() == null);
		assertEquals(HttpStatus.OK.value(), responseApi.getCode());
		assertTrue(responseApi.getResult() != null);
	}

	@Test
	void testAddProject() throws Exception { // NOSONAR
		MockHttpServletRequest request = new MockHttpServletRequest();
		request.setMethod("POST");

		DHProject project = this.getFakeProject(1);

		ApiResponse<DHProject> apiResponse = new ApiResponse<>();
		apiResponse.setResponse(HttpStatus.OK, project, null, null, request);
		when(configurationService.addProject(any(HttpServletRequest.class), any(DHProject.class)))
		.thenReturn(apiResponse);

		String projectStr = objectMapper.writeValueAsString(project);
		ResultActions resultActions = this.testUtils.prepareResultActions(this.mockMvc, request.getMethod(),
				URL_PROJECTS_TEMPLATE, "api/v1/configurations/projects/post", projectStr);

		MvcResult result = resultActions.andReturn();
		String objString = result.getResponse().getContentAsString();

		TypeReference<ApiResponse<DHProject>> valueType = new TypeReference<ApiResponse<DHProject>>() {
		};
		ApiResponse<DHProject> responseApi = objectMapper.readValue(objString, valueType);

		assertTrue(responseApi.getErrors() == null);
		assertEquals(HttpStatus.OK.value(), responseApi.getCode());
		assertTrue(responseApi.getResult() != null);
		assertTrue(responseApi.getMessages() == null);
	}

	@Test
	void testUpdateProject() throws Exception { // NOSONAR
		MockHttpServletRequest request = new MockHttpServletRequest();
		request.setMethod("PUT");

		DHProject project = this.getFakeProject(1);

		ApiResponse<DHProject> apiResponse = new ApiResponse<>();
		apiResponse.setResponse(HttpStatus.OK, project, null, null, request);
		when(configurationService.updateProject(any(HttpServletRequest.class), any(DHProject.class)))
		.thenReturn(apiResponse);

		String projectStr = objectMapper.writeValueAsString(project);
		ResultActions resultActions = this.testUtils.prepareResultActions(this.mockMvc, request.getMethod(),
				URL_PROJECTS_TEMPLATE, "api/v1/configurations/projects/put", projectStr);

		MvcResult result = resultActions.andReturn();
		String objString = result.getResponse().getContentAsString();

		TypeReference<ApiResponse<DHProject>> valueType = new TypeReference<ApiResponse<DHProject>>() {
		};
		ApiResponse<DHProject> responseApi = objectMapper.readValue(objString, valueType);

		assertEquals(HttpStatus.OK.value(), responseApi.getCode());
		assertTrue(responseApi.getResult() != null);

	}

	@Test
	void testDeleteProject() throws Exception { // NOSONAR
		MockHttpServletRequest request = new MockHttpServletRequest();
		request.setMethod("DELETE");

		DHProject project = this.getFakeProject(1);

		ApiResponse<DHProject> apiResponse = new ApiResponse<>();
		apiResponse.setResponse(HttpStatus.OK, project, null, null, request);
		when(configurationService.deleteProject(any(HttpServletRequest.class), any(String.class)))
		.thenReturn(apiResponse);

		ResultActions resultActions = this.testUtils.prepareResultActions(this.mockMvc, request.getMethod(),
				URL_PROJECTS_TEMPLATE + "/" + project.getId(), "api/v1/configurations/projects/delete", null);

		MvcResult result = resultActions.andReturn();
		String objString = result.getResponse().getContentAsString();

		TypeReference<ApiResponse<DHProject>> valueType = new TypeReference<ApiResponse<DHProject>>() {
		};
		ApiResponse<DHProject> responseApi = objectMapper.readValue(objString, valueType);

		assertTrue(responseApi.getResult() != null);
		assertEquals(HttpStatus.OK.value(), responseApi.getCode());
	}

	@Test
	void testProjectsImages() throws Exception { // NOSONAR
		MockHttpServletRequest request = new MockHttpServletRequest();
		request.setMethod("GET");

		List<String> images = new ArrayList<>();
		images.add("image-filename-1");
		images.add("image-filename-2");

		ApiResponse<List<String>> apiResponse = new ApiResponse<>();
		apiResponse.setResponse(HttpStatus.OK, images, null, null, request);
		when(configurationService.projectImages(any(HttpServletRequest.class))).thenReturn(apiResponse);

		ResultActions resultActions = this.testUtils.prepareResultActions(this.mockMvc, request.getMethod(),
				"%s/v1/images/projects", "api/v1/configurations/projects/images", null);

		MvcResult result = resultActions.andReturn();
		String objString = result.getResponse().getContentAsString();

		TypeReference<ApiResponse<List<String>>> valueType = new TypeReference<ApiResponse<List<String>>>() {
		};
		ApiResponse<List<String>> responseApi = objectMapper.readValue(objString, valueType);

		assertEquals(HttpStatus.OK.value(), responseApi.getCode());
		assertTrue(responseApi.getErrors() == null);
		assertTrue(responseApi.getMessages() == null);
		assertTrue(!responseApi.getResult().isEmpty());
	}
	private DHConfiguration getFakeConfiguration() {
		DHConfiguration dhConfiguration = new DHConfiguration();
		dhConfiguration.setId("datahub-default-configuration");
		dhConfiguration.setName("Datahub Default Configuration");

		for (int i = 0; i < 3; i++) {
			DHProject project = getFakeProject(i);
			dhConfiguration.getProjects().add(project);
		}

		return dhConfiguration;
	}

	private DHProject getFakeProject(int index) {
		DHProject project = new DHProject();
		project.setId(UUID.randomUUID().toString());
		project.setDescription(String.format("Description Project %s", index));
		project.setEnabled(true);
		project.setImageFileName("http://example.com/image");
		project.setLastModified(new Date());
		project.setName(String.format("Project-%s", index));
		project.setOrderPopular(Long.valueOf(index));
		project.setPopular(false);
		return project;
	}

}
