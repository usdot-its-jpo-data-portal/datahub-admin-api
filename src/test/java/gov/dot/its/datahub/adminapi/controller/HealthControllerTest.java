package gov.dot.its.datahub.adminapi.controller;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import java.util.Date;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import gov.dot.its.datahub.adminapi.model.ApiResponse;
import gov.dot.its.datahub.adminapi.testutils.TestUtils;

@RunWith(SpringRunner.class)
@WebMvcTest(HealthController.class)
@AutoConfigureRestDocs(outputDir = "target/generated-snippets", uriHost = "example.com", uriPort = 3008, uriScheme = "http")
@ComponentScan("gov.dot.its.datahub.adminapi.testutils")
public class HealthControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private TestUtils testUtils;

	@Autowired
	private ObjectMapper objectMapper;

	@Test
	public void testHealthCheck() throws Exception { // NOSONAR
		MockHttpServletRequest request = new MockHttpServletRequest();
		request.setMethod("GET");

		ApiResponse<Date> apiResponse = new ApiResponse<>();
		apiResponse.setResponse(HttpStatus.OK, new Date(), null, null, request);

		ResultActions resultActions = this.testUtils.prepareResultActions(this.mockMvc, request.getMethod(),
				"%s/health", "api/health/data", "");

		MvcResult result = resultActions.andReturn();
		String objString = result.getResponse().getContentAsString();

		TypeReference<ApiResponse<Date>> valueType = new TypeReference<ApiResponse<Date>>() {
		};
		ApiResponse<Date> responseApi = objectMapper.readValue(objString, valueType);

		assertEquals(HttpStatus.OK.value(), responseApi.getCode());
		assertNotNull(responseApi.getResult());
		assertNull(responseApi.getErrors());
		assertNull(responseApi.getMessages());
	}

}
