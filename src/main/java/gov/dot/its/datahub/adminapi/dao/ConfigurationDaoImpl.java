package gov.dot.its.datahub.adminapi.dao;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.script.Script;
import org.elasticsearch.script.ScriptType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import gov.dot.its.datahub.adminapi.model.DHConfiguration;
import gov.dot.its.datahub.adminapi.model.DHDataType;
import gov.dot.its.datahub.adminapi.model.DHEngagementPopup;
import gov.dot.its.datahub.adminapi.model.DHProject;

@Repository
public class ConfigurationDaoImpl implements ConfigurationDao {

	static final String ES_SCRIPT_PAINLESS = "painless";

	@Value("${datahub.admin.api.configurations.index}")
	private String configurationsIndex;

	@Value("${datahub.admin.api.configurations.default}")
	private String configurationId;

	@Value("${datahub.admin.api.configurations.images.list}")
	private String imagesList;

	@Value("${datahub.admin.api.configurations.images.path}")
	private String imagesPath;

	@Autowired
	private ObjectMapper objectMapper;

	private RestHighLevelClient restHighLevelClient;

	public ConfigurationDaoImpl(RestHighLevelClient restHighLevelClient) {
		this.restHighLevelClient = restHighLevelClient;
	}

	@Override
	public DHConfiguration getConfiguration() throws IOException {
		GetRequest getRequest = new GetRequest(configurationsIndex, configurationId);
		GetResponse getResponse = restHighLevelClient.get(getRequest, RequestOptions.DEFAULT);
		if (!getResponse.isExists()) {
			return null;
		}

		Map<String, Object> sourceMap = getResponse.getSourceAsMap();

		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

		return mapper.convertValue(sourceMap, DHConfiguration.class);
	}

	@Override
	public List<DHProject> getProjects() throws IOException {
		GetRequest getRequest = new GetRequest(configurationsIndex, configurationId);
		GetResponse getResponse = restHighLevelClient.get(getRequest, RequestOptions.DEFAULT);
		if (!getResponse.isExists()) {
			return new ArrayList<>();
		}

		Map<String, Object> sourceMap = getResponse.getSourceAsMap();

		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		DHConfiguration configuration = mapper.convertValue(sourceMap, DHConfiguration.class);

		return configuration.getProjects();
	}

	@Override
	public DHProject getProjectById(String id) throws IOException {
		DHConfiguration configuration = getConfiguration();
		if (configuration == null) {
			return null;
		}

		for (DHProject project : configuration.getProjects()) {
			if (project.getId().equalsIgnoreCase(id)) {
				return project;
			}
		}

		return null;
	}

	@Override
	public String addProject(DHProject chProject) throws IOException {
		@SuppressWarnings("unchecked")
		Map<String, Object> jsonData = objectMapper.convertValue(chProject, Map.class);
		Map<String, Object> param = new HashMap<>();
		param.put("pro", jsonData);

		String scriptCode = ""
				+ "int found_index = -1;"
				+ "if (ctx._source.projects == null ) {"
				+ "  ctx._source.projects = [];"
				+ "}"
				+ "for(int i=0;i<ctx._source.projects.length;i++) {"
				+ "  if(ctx._source.projects[i].id == params.pro.id){"
				+ "    found_index = i;"
				+ "    break;"
				+ "  }"
				+ "}"
				+ "if (found_index < 0) {"
				+ "  ctx._source.projects.add(params.pro);"
				+ "}";

		Script inline = new Script(ScriptType.INLINE, ES_SCRIPT_PAINLESS, scriptCode, param);

		UpdateRequest updateRequest = new UpdateRequest(configurationsIndex, configurationId);
		updateRequest.script(inline);

		UpdateResponse updateResponse = restHighLevelClient.update(updateRequest, RequestOptions.DEFAULT);

		return updateResponse.getResult().name();
	}

	@Override
	public String updateProject(DHProject chProject) throws IOException {
		@SuppressWarnings("unchecked")
		Map<String, Object> jsonData = objectMapper.convertValue(chProject, Map.class);
		Map<String, Object> param = new HashMap<>();
		param.put("pro", jsonData);

		String scriptCode = ""
				+ "if (ctx._source.projects != null) {"
				+ "  for(int i=0;i<ctx._source.projects.length;i++) {"
				+ "    if(ctx._source.projects[i].id == params.pro.id){"
				+ "      ctx._source.projects[i] = params.pro"
				+ "    }"
				+ "  }"
				+ "}";

		Script inline = new Script(ScriptType.INLINE, ES_SCRIPT_PAINLESS, scriptCode, param);

		UpdateRequest updateRequest = new UpdateRequest(configurationsIndex, configurationId);
		updateRequest.script(inline);

		UpdateResponse updateResponse = restHighLevelClient.update(updateRequest, RequestOptions.DEFAULT);

		return updateResponse.getResult().name();
	}

	@Override
	public boolean deleteProjectById(String id) throws IOException {
		Map<String, Object> param = new HashMap<>();
		param.put("proId", id);

		String scriptCode = ""
				+ "int remove_index = -1;"
				+ "for(int i=0;i<ctx._source.projects.length;i++) {"
				+ "  if(ctx._source.projects[i].id == params.proId){"
				+ "    remove_index = i;"
				+ "    break;"
				+ "  }"
				+ "}"
				+ "if (remove_index >= 0) {"
				+ "  ctx._source.projects.remove(remove_index);"
				+ "}";

		Script inline = new Script(ScriptType.INLINE, ES_SCRIPT_PAINLESS, scriptCode, param);

		UpdateRequest updateRequest = new UpdateRequest(configurationsIndex, configurationId);
		updateRequest.script(inline);

		UpdateResponse updateResponse = restHighLevelClient.update(updateRequest, RequestOptions.DEFAULT);

		return updateResponse.getResult().name() != null;
	}

	@Override
	public List<String> getProjectImages() throws IOException {
		ObjectMapper mapper = new ObjectMapper();

		TypeReference<List<String>> typeRef = new TypeReference<List<String>>() {
		};
		List<String> images = mapper.readValue(new URL(this.imagesList), typeRef);
		for (int i = 0; i < images.size(); i++) {
			images.set(i, String.format("%s/%s", this.imagesPath, images.get(i)));
		}
		return images;
	}

	@Override
	public List<DHDataType> getDataTypes() throws IOException {
		GetRequest getRequest = new GetRequest(configurationsIndex, configurationId);
		GetResponse getResponse = restHighLevelClient.get(getRequest, RequestOptions.DEFAULT);
		if (!getResponse.isExists()) {
			return new ArrayList<>();
		}

		Map<String, Object> sourceMap = getResponse.getSourceAsMap();

		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		DHConfiguration configuration = mapper.convertValue(sourceMap, DHConfiguration.class);

		return configuration.getDataTypes();
	}

	@Override
	public DHDataType getDataTypeById(String id) throws IOException {
		DHConfiguration configuration = getConfiguration();
		if (configuration == null) {
			return null;
		}

		for (DHDataType dataType : configuration.getDataTypes()) {
			if (dataType.getId().equalsIgnoreCase(id)) {
				return dataType;
			}
		}

		return null;
	}

	@Override
	public String addDataType(DHDataType dhDataType) throws IOException {
		@SuppressWarnings("unchecked")
		Map<String, Object> jsonData = objectMapper.convertValue(dhDataType, Map.class);
		Map<String, Object> param = new HashMap<>();
		param.put("dt", jsonData);

		String scriptCode = ""
				+ "int found_index = -1;"
				+ "if (ctx._source.dataTypes.empty) {"
				+ "  ctx._source.dataTypes = [];"
				+ "}"
				+ "for(int i=0;i<ctx._source.dataTypes.length;i++) {"
				+ "  if(ctx._source.dataTypes[i].id == params.dt.id){"
				+ "    found_index = i;"
				+ "    break;"
				+ "  }"
				+ "}"
				+ "if (found_index < 0) {"
				+ "  ctx._source.dataTypes.add(params.dt);"
				+ "}";

		Script inline = new Script(ScriptType.INLINE, ES_SCRIPT_PAINLESS, scriptCode, param);

		UpdateRequest updateRequest = new UpdateRequest(configurationsIndex, configurationId);
		updateRequest.script(inline);

		UpdateResponse updateResponse = restHighLevelClient.update(updateRequest, RequestOptions.DEFAULT);

		return updateResponse.getResult().name();
	}

	@Override
	public String updateDataType(DHDataType dhDataType) throws IOException {
		@SuppressWarnings("unchecked")
		Map<String, Object> jsonData = objectMapper.convertValue(dhDataType, Map.class);
		Map<String, Object> param = new HashMap<>();
		param.put("dt", jsonData);

		String scriptCode = ""
				+ "if (ctx._source.dataTypes != null) {"
				+ "  for(int i=0;i<ctx._source.dataTypes.length;i++) {"
				+ "    if(ctx._source.dataTypes[i].id == params.dt.id){"
				+ "      ctx._source.dataTypes[i] = params.dt"
				+ "    }"
				+ "  }"
				+ "}";

		Script inline = new Script(ScriptType.INLINE, ES_SCRIPT_PAINLESS, scriptCode, param);

		UpdateRequest updateRequest = new UpdateRequest(configurationsIndex, configurationId);
		updateRequest.script(inline);

		UpdateResponse updateResponse = restHighLevelClient.update(updateRequest, RequestOptions.DEFAULT);

		return updateResponse.getResult().name();
	}

	@Override
	public boolean deleteDataTypeById(String id) throws IOException {
		Map<String, Object> param = new HashMap<>();
		param.put("dataTypeId", id);

		String scriptCode = ""
				+ "int remove_index = -1;"
				+ "for(int i=0;i<ctx._source.dataTypes.length;i++) {"
				+ "  if(ctx._source.dataTypes[i].id == params.dataTypeId){"
				+ "    remove_index = i;"
				+ "    break;"
				+ "  }"
				+ "}"
				+ "if (remove_index >= 0) {"
				+ "  ctx._source.dataTypes.remove(remove_index);"
				+ "}";

		Script inline = new Script(ScriptType.INLINE, ES_SCRIPT_PAINLESS, scriptCode, param);

		UpdateRequest updateRequest = new UpdateRequest(configurationsIndex, configurationId);
		updateRequest.script(inline);

		UpdateResponse updateResponse = restHighLevelClient.update(updateRequest, RequestOptions.DEFAULT);

		return updateResponse.getResult().name() != null;
	}

	@Override
	public List<DHEngagementPopup> getEngagementPopups() throws IOException {
		GetRequest getRequest = new GetRequest(configurationsIndex, configurationId);
		GetResponse getResponse = restHighLevelClient.get(getRequest, RequestOptions.DEFAULT);
		if (!getResponse.isExists()) {
			return new ArrayList<>();
		}

		Map<String, Object> sourceMap = getResponse.getSourceAsMap();

		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		DHConfiguration configuration = mapper.convertValue(sourceMap, DHConfiguration.class);

		return configuration.getEngagementPopups();
	}

	@Override
	public String addEngagementPopup(DHEngagementPopup engagementPopup) throws IOException {
		@SuppressWarnings("unchecked")
		Map<String, Object> jsonData = objectMapper.convertValue(engagementPopup, Map.class);
		Map<String, Object> param = new HashMap<>();
		param.put("enpo", jsonData);

		String scriptCode = ""
				+ "int found_index = -1;"
				+ "for(int i=0;i<ctx._source.engagementPopups.length;i++) {"
				+ "  if(ctx._source.engagementPopups[i].id == params.enpo.id){"
				+ "    found_index = i;"
				+ "    break;"
				+ "  }"
				+ "}"
				+ "if (found_index < 0) {"
				+ "  ctx._source.engagementPopups.add(params.enpo);"
				+ "}";
		
		Script inline = new Script(ScriptType.INLINE, ES_SCRIPT_PAINLESS, scriptCode, param);

		UpdateRequest updateRequest = new UpdateRequest(configurationsIndex, configurationId);
		updateRequest.script(inline);

		UpdateResponse updateResponse = restHighLevelClient.update(updateRequest, RequestOptions.DEFAULT);

		return updateResponse.getResult().name();
	}

	@Override
	public String updateEngagementPopup(DHEngagementPopup engagementPopup) throws IOException {
		@SuppressWarnings("unchecked")
		Map<String, Object> jsonData = objectMapper.convertValue(engagementPopup, Map.class);
		Map<String, Object> param = new HashMap<>();
		param.put("enpo", jsonData);

		String scriptCode = ""
				+ "for(int i=0;i<ctx._source.engagementPopups.length;i++) {"
				+ "  if (params.enpo.isActive == true) {"
				+ "    if(ctx._source.engagementPopups[i].id != params.enpo.id) {"
				+ "       if(ctx._source.engagementPopups[i].isActive == true) {"
				+ "         ctx._source.engagementPopups[i].isActive = false;"
				+ "       }"
				+ "    }"
				+ "  }"
				+ "  if(ctx._source.engagementPopups[i].id == params.enpo.id){"
				+ "    ctx._source.engagementPopups[i] = params.enpo"
				+ "  }"
				+ "}";

		Script inline = new Script(ScriptType.INLINE, ES_SCRIPT_PAINLESS, scriptCode, param);

		UpdateRequest updateRequest = new UpdateRequest(configurationsIndex, configurationId);
		updateRequest.script(inline);

		UpdateResponse updateResponse = restHighLevelClient.update(updateRequest, RequestOptions.DEFAULT);

		return updateResponse.getResult().name();
	}

	@Override
	public boolean removeEngagementPopup(String id) throws IOException {
		Map<String, Object> param = new HashMap<>();
		param.put("enpoId", id);

		String scriptCode = ""
				+ "int remove_index = -1;"
				+ "for(int i=0;i<ctx._source.engagementPopups.length;i++) {"
				+ "  if(ctx._source.engagementPopups[i].id == params.enpoId){"
				+ "    remove_index = i;"
				+ "    break;"
				+ "  }"
				+ "}"
				+ "if (remove_index >= 0) {"
				+ "  ctx._source.engagementPopups.remove(remove_index);"
				+ "}";

		Script inline = new Script(ScriptType.INLINE, ES_SCRIPT_PAINLESS, scriptCode, param);

		UpdateRequest updateRequest = new UpdateRequest(configurationsIndex, configurationId);
		updateRequest.script(inline);

		UpdateResponse updateResponse = restHighLevelClient.update(updateRequest, RequestOptions.DEFAULT);

		return updateResponse.getResult().name() != null;
	}

}
