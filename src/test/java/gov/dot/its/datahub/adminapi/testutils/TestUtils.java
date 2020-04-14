package gov.dot.its.datahub.adminapi.testutils;

import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.MediaType;
import org.springframework.restdocs.operation.preprocess.Preprocessors;
import org.springframework.stereotype.Component;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

@Component
public class TestUtils {
	private static final String SERVER_SERVLET_CONTEXT_PATH = "server.servlet.context-path";
	private static final String HEADER_HOST = "Host";
	private static final String HEADER_CONTENT_LENGTH = "Content-Length";
	private static final String SECURITY_TOKEN_NAME = "datahub.admin.api.security.token.name";
	private static final String SECURITY_TOKEN_KEY = "datahub.admin.api.security.token.key";

	@Autowired
	private Environment env;

	public ResultActions prepareResultActions(MockMvc mockMvc, String requestMethod, String testUrlTemplate,
			String documentPath, String objStr)  throws Exception { // NOSONAR
		return this.prepareResultActions(mockMvc, requestMethod, testUrlTemplate, documentPath, objStr, false);
	}

	public ResultActions prepareResultActions(MockMvc mockMvc, String requestMethod, String testUrlTemplate,
			String documentPath, String objStr,boolean noTokenHeader) throws Exception { // NOSONAR
		String tokenKey = env.getProperty(SECURITY_TOKEN_KEY);
		String tokenName = env.getProperty(SECURITY_TOKEN_NAME);

		MockHttpServletRequestBuilder request = null;
		switch (requestMethod) {
		case "GET":
			request = get(String.format(testUrlTemplate, env.getProperty(SERVER_SERVLET_CONTEXT_PATH)))
			.contextPath(String.format("%s", env.getProperty(SERVER_SERVLET_CONTEXT_PATH)));
			break;
		case "POST":
			request = post(String.format(testUrlTemplate, env.getProperty(SERVER_SERVLET_CONTEXT_PATH)))
			.contentType(MediaType.APPLICATION_JSON)
			.contextPath(String.format("%s", env.getProperty(SERVER_SERVLET_CONTEXT_PATH))).content(objStr);
			break;
		case "PUT":
			request = put(String.format(testUrlTemplate, env.getProperty(SERVER_SERVLET_CONTEXT_PATH)))
			.contentType(MediaType.APPLICATION_JSON)
			.contextPath(String.format("%s", env.getProperty(SERVER_SERVLET_CONTEXT_PATH))).content(objStr);
			break;
		case "DELETE":
			request = delete(String.format(testUrlTemplate, env.getProperty(SERVER_SERVLET_CONTEXT_PATH)))
			.contextPath(String.format("%s", env.getProperty(SERVER_SERVLET_CONTEXT_PATH)));
			break;
		default:
			request = null;
		}

		if (request != null && !noTokenHeader) {
			request.header(tokenName, tokenKey);
		}

		return mockMvc.perform(request).andExpect(status().isOk())
				.andDo(document(documentPath,
						Preprocessors.preprocessRequest(Preprocessors.prettyPrint(),
								Preprocessors.removeHeaders(HEADER_HOST, HEADER_CONTENT_LENGTH)),
						Preprocessors.preprocessResponse(Preprocessors.prettyPrint(),
								Preprocessors.removeHeaders(HEADER_HOST, HEADER_CONTENT_LENGTH))));
	}
}
