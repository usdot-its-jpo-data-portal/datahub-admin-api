package gov.dot.its.datahub.adminapi.controller;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.security.SecureRandom;
import java.sql.Timestamp;
import java.util.ArrayList;
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
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import gov.dot.its.datahub.adminapi.business.DataAssetsService;
import gov.dot.its.datahub.adminapi.model.ApiResponse;
import gov.dot.its.datahub.adminapi.model.DataAsset;
import gov.dot.its.datahub.adminapi.model.Metrics;
import gov.dot.its.datahub.adminapi.testutils.TestUtils;

@RunWith(SpringRunner.class)
@WebMvcTest(DataAssetsController.class)
@AutoConfigureRestDocs(outputDir = "target/generated-snippets", uriHost = "example.com", uriPort = 3008, uriScheme = "http")
@ComponentScan("gov.dot.its.datahub.adminapi.testutils")
class DataAssetsControllerTest {

	private static final String URL_DATAASSETS_TEMPLATE = "%s/v1/dataassets";
	private static final String SECURITY_TOKEN_KEY = "datahub.admin.api.security.token.key";

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private TestUtils testUtils;

	@Autowired
	private ObjectMapper objectMapper;

	@MockBean
	private DataAssetsService dataAssetsService;

	static {
		System.setProperty(SECURITY_TOKEN_KEY, "123");
	}

	private SecureRandom random;

	public DataAssetsControllerTest() {
		this.random = new SecureRandom();
	}

	@Test
	void testDataassets() throws Exception { // NOSONAR
		MockHttpServletRequest request = new MockHttpServletRequest();
		request.setMethod("GET");

		List<DataAsset> dataAssets = this.getFakeDataAssets();

		ApiResponse<List<DataAsset>> apiResponse = new ApiResponse<>();
		apiResponse.setResponse(HttpStatus.OK, dataAssets, null, null, request);

		when(dataAssetsService.dataAssets(any(HttpServletRequest.class))).thenReturn(apiResponse);
		ResultActions resultActions = this.testUtils.prepareResultActions(this.mockMvc, request.getMethod(),
				URL_DATAASSETS_TEMPLATE, "api/v1/dataassets/get", "");

		MvcResult result = resultActions.andReturn();
		String objString = result.getResponse().getContentAsString();

		TypeReference<ApiResponse<List<DataAsset>>> valueType = new TypeReference<ApiResponse<List<DataAsset>>>() {
		};
		ApiResponse<List<DataAsset>> responseApi = objectMapper.readValue(objString, valueType);

		assertEquals(HttpStatus.OK.value(), responseApi.getCode());
		assertTrue(!responseApi.getResult().isEmpty());
		assertTrue(responseApi.getErrors() == null);
		assertTrue(responseApi.getMessages() == null);
	}

	@Test
	void testDataasset() throws Exception { // NOSONAR
		MockHttpServletRequest request = new MockHttpServletRequest();
		request.setMethod("GET");

		DataAsset dataAsset = this.getFakeDataAsset(1);

		ApiResponse<DataAsset> apiResponse = new ApiResponse<>();
		apiResponse.setResponse(HttpStatus.OK, dataAsset, null, null, request);

		when(dataAssetsService.dataAsset(any(HttpServletRequest.class), any(String.class))).thenReturn(apiResponse);
		ResultActions resultActions = this.testUtils.prepareResultActions(this.mockMvc, request.getMethod(),
				URL_DATAASSETS_TEMPLATE + "/" + dataAsset.getDhId(), "api/v1/dataassets/get-id", "");

		MvcResult result = resultActions.andReturn();
		String objString = result.getResponse().getContentAsString();

		TypeReference<ApiResponse<DataAsset>> valueType = new TypeReference<ApiResponse<DataAsset>>() {
		};
		ApiResponse<DataAsset> responseApi = objectMapper.readValue(objString, valueType);

		assertEquals(HttpStatus.OK.value(), responseApi.getCode());
		assertTrue(responseApi.getResult() != null);
		assertTrue(responseApi.getErrors() == null);
		assertTrue(responseApi.getMessages() == null);
	}

	@Test
	void testUpdateDataAsset() throws Exception { // NOSONAR
		MockHttpServletRequest request = new MockHttpServletRequest();
		request.setMethod("PUT");

		DataAsset dataAsset = this.getFakeDataAsset(1);

		ApiResponse<DataAsset> apiResponse = new ApiResponse<>();
		apiResponse.setResponse(HttpStatus.OK, dataAsset, null, null, request);

		when(dataAssetsService.updateDataAsset(any(HttpServletRequest.class), any(DataAsset.class)))
				.thenReturn(apiResponse);
		String dataAssetStr = objectMapper.writeValueAsString(dataAsset);
		ResultActions resultActions = this.testUtils.prepareResultActions(this.mockMvc, request.getMethod(),
				URL_DATAASSETS_TEMPLATE, "api/v1/dataassets/put", dataAssetStr);

		MvcResult result = resultActions.andReturn();
		String objString = result.getResponse().getContentAsString();

		TypeReference<ApiResponse<DataAsset>> valueType = new TypeReference<ApiResponse<DataAsset>>() {
		};
		ApiResponse<DataAsset> responseApi = objectMapper.readValue(objString, valueType);

		assertEquals(HttpStatus.OK.value(), responseApi.getCode());
		assertTrue(responseApi.getResult() != null);
		assertTrue(responseApi.getErrors() == null);
		assertTrue(responseApi.getMessages() == null);
	}

	private List<DataAsset> getFakeDataAssets() {
		List<DataAsset> dataAssets = new ArrayList<>();

		for (int i = 0; i < 3; i++) {
			DataAsset dataAsset = this.getFakeDataAsset(i);
			dataAssets.add(dataAsset);
		}

		return dataAssets;
	}

	public DataAsset getFakeDataAsset(int index) {
		DataAsset dataAsset = new DataAsset();
		dataAsset.setAccessLevel("Public");
		dataAsset.setDescription("Description");
		dataAsset.setDhId(UUID.randomUUID().toString());
		dataAsset.setDhLastUpdate(new Timestamp(System.currentTimeMillis()));

		List<String> dhProjects = new ArrayList<>();
		for (int j = 0; j < 2; j++) {
			dhProjects.add(UUID.randomUUID().toString());
		}
		dataAsset.setDhProjects(dhProjects);

		dataAsset.setDhSourceName("source-name");
		dataAsset.setEsScore(1F);
		dataAsset.setId(UUID.randomUUID().toString());
		dataAsset.setLastUpdate(new Timestamp(System.currentTimeMillis()));

		Metrics metrics = new Metrics();
		metrics.setDownloadsTotal(Long.valueOf(this.random.nextInt(1000)));
		metrics.setPageViewsLastMonth(Long.valueOf(this.random.nextInt(500)));
		metrics.setPageViewsTotal(Long.valueOf(this.random.nextInt(1200)));

		dataAsset.setMetrics(metrics);

		dataAsset.setName(String.format("DataAsset-%s", index));
		dataAsset.setSourceUrl("http://to.source.url");

		List<String> tags = new ArrayList<>();
		for (int k = 1; k <= 3; k++) {
			tags.add(String.format("tag-%s", k));
		}

		dataAsset.setTags(tags);

		return dataAsset;
	}
}
