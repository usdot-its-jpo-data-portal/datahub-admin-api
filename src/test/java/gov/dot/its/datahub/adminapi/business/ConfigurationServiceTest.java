package gov.dot.its.datahub.adminapi.business;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.context.junit4.SpringRunner;

import gov.dot.its.datahub.adminapi.MockDataConfiguration;
import gov.dot.its.datahub.adminapi.dao.ConfigurationDao;

import gov.dot.its.datahub.adminapi.model.ApiError;
import gov.dot.its.datahub.adminapi.model.ApiResponse;

import gov.dot.its.datahub.adminapi.model.DHConfiguration;
import gov.dot.its.datahub.adminapi.model.DHEngagementPopup;
import gov.dot.its.datahub.adminapi.utils.ApiUtils;


@RunWith(SpringRunner.class)
public class ConfigurationServiceTest {

	private final String TEST_HTTP_GET = "GET";
	private final String TEST_TOKEN_KEY = "CHTOKEN";
	private final String TEST_TOKEN_VALUE = "change-it";
	private final String TEST_MESSAGE = "Testing...";
	private final String TEST_ID = "1";

	private MockDataConfiguration mockData;

	@InjectMocks
	private ConfigurationServiceImpl configurationService;

	@Mock
	private ConfigurationDao configurationDao;

	@Mock
	private ApiUtils apiUtils;

	@Before
	public void setUp() {
		MockitoAnnotations.initMocks(this);
		mockData = new MockDataConfiguration();
	}

	@Test
	public void testConfigurationData() throws IOException {
		final MockHttpServletRequest request = new MockHttpServletRequest();
		request.setMethod(TEST_HTTP_GET);
		request.addHeader(TEST_TOKEN_KEY, TEST_TOKEN_VALUE);

		DHConfiguration configuration = this.mockData.getFakeConfiguration();

		when(configurationDao.getConfiguration()).thenReturn(configuration);

		ApiResponse<DHConfiguration> apiResponse = configurationService.configurations(request);
		assertNotNull(apiResponse);
		assertEquals(HttpStatus.OK.value(), apiResponse.getCode());
		assertNotNull(apiResponse.getResult());
	}

	@Test
	public void testConfigurationNoData() throws IOException {
		final MockHttpServletRequest request = new MockHttpServletRequest();
		request.setMethod(TEST_HTTP_GET);
		request.addHeader(TEST_TOKEN_KEY, TEST_TOKEN_VALUE);

		when(configurationDao.getConfiguration()).thenReturn(null);

		ApiResponse<DHConfiguration> apiResponse = configurationService.configurations(request);
		assertNotNull(apiResponse);
		assertEquals(HttpStatus.NO_CONTENT.value(), apiResponse.getCode());
		assertNull(apiResponse.getResult());
	}

	@Test
	public void testConfigurationError() throws IOException {
		final MockHttpServletRequest request = new MockHttpServletRequest();
		request.setMethod(TEST_HTTP_GET);
		request.addHeader(TEST_TOKEN_KEY, TEST_TOKEN_VALUE);
		final List<ApiError> errors = new ArrayList<>();
		errors.add(new ApiError(TEST_MESSAGE));

		when(apiUtils.getErrorsFromException(anyList(), any(Exception.class))).thenReturn(errors);
		when(configurationDao.getConfiguration()).thenThrow(new IOException());

		ApiResponse<DHConfiguration> apiResponse = configurationService.configurations(request);
		assertNotNull(apiResponse);
		assertEquals(HttpStatus.INTERNAL_SERVER_ERROR.value(), apiResponse.getCode());
		assertNull(apiResponse.getResult());
		assertFalse(apiResponse.getErrors().isEmpty());
	}

	@Test
	public void testEngagementPopupsNoData() throws IOException {
		final MockHttpServletRequest request = new MockHttpServletRequest();
		request.setMethod(TEST_HTTP_GET);
		request.addHeader(TEST_TOKEN_KEY, TEST_TOKEN_VALUE);

		when(configurationDao.getEngagementPopups()).thenReturn(new ArrayList<>());

		ApiResponse<List<DHEngagementPopup>> apiResponse = configurationService.engagementpopups(request);
		assertNotNull(apiResponse);
		assertEquals(HttpStatus.NO_CONTENT.value(), apiResponse.getCode());
		assertNull(apiResponse.getResult());
	}

	@Test
	public void testEngagementPopupsException() throws IOException {
		final MockHttpServletRequest request = new MockHttpServletRequest();
		request.setMethod(TEST_HTTP_GET);
		request.addHeader(TEST_TOKEN_KEY, TEST_TOKEN_VALUE);

		List<ApiError> errors = new ArrayList<>();
		errors.add(new ApiError(TEST_MESSAGE));

		when(apiUtils.getErrorsFromException(anyList(), any(Exception.class))).thenReturn(errors);
		when(configurationDao.getEngagementPopups()).thenThrow(new IOException());

		ApiResponse<List<DHEngagementPopup>> apiResponse = configurationService.engagementpopups(request);
		assertNotNull(apiResponse);
		assertEquals(HttpStatus.INTERNAL_SERVER_ERROR.value(), apiResponse.getCode());
		assertNull(apiResponse.getResult());
		assertFalse(apiResponse.getErrors().isEmpty());
	}

	@Test
	public void testUpdateEngagementPopupOk() throws IOException {
		final MockHttpServletRequest request = new MockHttpServletRequest();
		request.setMethod(TEST_HTTP_GET);
		request.addHeader(TEST_TOKEN_KEY, TEST_TOKEN_VALUE);

		DHEngagementPopup engagementPopup = mockData.getFakeEngagementPopup(TEST_ID);

		when(configurationDao.updateEngagementPopup(any(DHEngagementPopup.class))).thenReturn(TEST_MESSAGE);

		ApiResponse<DHEngagementPopup> apiResponse = configurationService.updateEngagementPopup(request, engagementPopup);
		assertNotNull(apiResponse);
		assertEquals(HttpStatus.OK.value(), apiResponse.getCode());
		assertNotNull(apiResponse.getResult());
	}

	@Test
	public void testUpdateEngagementPopupFail() throws IOException {
		final MockHttpServletRequest request = new MockHttpServletRequest();
		request.setMethod(TEST_HTTP_GET);
		request.addHeader(TEST_TOKEN_KEY, TEST_TOKEN_VALUE);

		DHEngagementPopup engagementPopup = mockData.getFakeEngagementPopup(TEST_ID);

		when(configurationDao.updateEngagementPopup(any(DHEngagementPopup.class))).thenReturn(null);

		ApiResponse<DHEngagementPopup> apiResponse = configurationService.updateEngagementPopup(request, engagementPopup);
		assertNotNull(apiResponse);
		assertEquals(HttpStatus.INTERNAL_SERVER_ERROR.value(), apiResponse.getCode());
		assertNull(apiResponse.getResult());
	}

	@Test
	public void testUpdateEngagementPopupException() throws IOException {
		final MockHttpServletRequest request = new MockHttpServletRequest();
		request.setMethod(TEST_HTTP_GET);
		request.addHeader(TEST_TOKEN_KEY, TEST_TOKEN_VALUE);

		List<ApiError> errors = new ArrayList<>();
		errors.add(new ApiError(TEST_MESSAGE));

		DHEngagementPopup engagementPopup = mockData.getFakeEngagementPopup(TEST_ID);

		when(apiUtils.getErrorsFromException(anyList(), any(Exception.class))).thenReturn(errors);
		when(configurationDao.updateEngagementPopup(any(DHEngagementPopup.class))).thenThrow(new IOException());

		ApiResponse<DHEngagementPopup> apiResponse = configurationService.updateEngagementPopup(request, engagementPopup);
		assertNotNull(apiResponse);
		assertEquals(HttpStatus.INTERNAL_SERVER_ERROR.value(), apiResponse.getCode());
		assertNull(apiResponse.getResult());
		assertFalse(apiResponse.getErrors().isEmpty());
	}

	@Test
	public void testAddEngagementPopupOk() throws IOException {
		final MockHttpServletRequest request = new MockHttpServletRequest();
		request.setMethod(TEST_HTTP_GET);
		request.addHeader(TEST_TOKEN_KEY, TEST_TOKEN_VALUE);

		DHEngagementPopup engagementPopup = mockData.getFakeEngagementPopup(TEST_ID);

		when(configurationDao.addEngagementPopup(any(DHEngagementPopup.class))).thenReturn(TEST_MESSAGE);

		ApiResponse<DHEngagementPopup> apiResponse = configurationService.addEngagementPopup(request, engagementPopup);
		assertNotNull(apiResponse);
		assertEquals(HttpStatus.OK.value(), apiResponse.getCode());
		assertNotNull(apiResponse.getResult());
	}

	@Test
	public void testAddEngagementPopupFail() throws IOException {
		final MockHttpServletRequest request = new MockHttpServletRequest();
		request.setMethod(TEST_HTTP_GET);
		request.addHeader(TEST_TOKEN_KEY, TEST_TOKEN_VALUE);

		DHEngagementPopup engagementPopup = mockData.getFakeEngagementPopup(TEST_ID);

		when(configurationDao.addEngagementPopup(any(DHEngagementPopup.class))).thenReturn(null);

		ApiResponse<DHEngagementPopup> apiResponse = configurationService.addEngagementPopup(request, engagementPopup);
		assertNotNull(apiResponse);
		assertEquals(HttpStatus.INTERNAL_SERVER_ERROR.value(), apiResponse.getCode());
		assertNull(apiResponse.getResult());
	}

	@Test
	public void testAddEngagementPopupException() throws IOException {
		final MockHttpServletRequest request = new MockHttpServletRequest();
		request.setMethod(TEST_HTTP_GET);
		request.addHeader(TEST_TOKEN_KEY, TEST_TOKEN_VALUE);

		DHEngagementPopup engagementPopup = mockData.getFakeEngagementPopup(TEST_ID);
		List<ApiError> errors = new ArrayList<>();
		errors.add(new ApiError(TEST_MESSAGE));

		when(apiUtils.getErrorsFromException(anyList(), any(Exception.class))).thenReturn(errors);
		when(configurationDao.addEngagementPopup(any(DHEngagementPopup.class))).thenThrow(new IOException());

		ApiResponse<DHEngagementPopup> apiResponse = configurationService.addEngagementPopup(request, engagementPopup);
		assertNotNull(apiResponse);
		assertEquals(HttpStatus.INTERNAL_SERVER_ERROR.value(), apiResponse.getCode());
		assertNull(apiResponse.getResult());
		assertFalse(apiResponse.getErrors().isEmpty());
	}

	@Test
	public void testDeleteEngagementPopupOk() throws IOException {
		final MockHttpServletRequest request = new MockHttpServletRequest();
		request.setMethod(TEST_HTTP_GET);
		request.addHeader(TEST_TOKEN_KEY, TEST_TOKEN_VALUE);

		when(configurationDao.removeEngagementPopup(anyString())).thenReturn(true);

		ApiResponse<DHEngagementPopup> apiResponse = configurationService.deleteEngagementPopup(request, TEST_ID);
		assertNotNull(apiResponse);
		assertEquals(HttpStatus.OK.value(), apiResponse.getCode());
		assertNull(apiResponse.getResult());
	}

	@Test
	public void testDeleteEngagementPopupException() throws IOException {
		final MockHttpServletRequest request = new MockHttpServletRequest();
		request.setMethod(TEST_HTTP_GET);
		request.addHeader(TEST_TOKEN_KEY, TEST_TOKEN_VALUE);

		List<ApiError> errors = new ArrayList<>();
		errors.add(new ApiError(TEST_MESSAGE));

		when(apiUtils.getErrorsFromException(anyList(), any(Exception.class))).thenReturn(errors);
		when(configurationDao.removeEngagementPopup(anyString())).thenThrow(new IOException());

		ApiResponse<DHEngagementPopup> apiResponse = configurationService.deleteEngagementPopup(request, TEST_ID);
		assertNotNull(apiResponse);
		assertEquals(HttpStatus.INTERNAL_SERVER_ERROR.value(), apiResponse.getCode());
		assertNull(apiResponse.getResult());
		assertFalse(apiResponse.getErrors().isEmpty());
	}
}
