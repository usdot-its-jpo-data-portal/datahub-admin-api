package gov.dot.its.datahub.adminapi.dao;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.FetchSourceContext;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import gov.dot.its.datahub.adminapi.model.DataAsset;

@Repository
public class DataAssetsDaoImpl implements DataAssetsDao {

	@Value("${datahub.admin.api.es.dataassets.index}")
	private String dataAssetsIndex;
	@Value("${datahub.admin.api.es.dataassets.fields}")
	private String[] includedFields;
	@Value("${datahub.admin.api.es.sort.by}")
	private String sortBy;
	@Value("${datahub.admin.api.es.sort.order}")
	private String sortOrder;
	@Value("${datahub.admin.api.es.dataassets.limit}")
	private Integer limit;

	private RestHighLevelClient restHighLevelClient;

	public DataAssetsDaoImpl(RestHighLevelClient restHighLevelClient) {
		this.restHighLevelClient = restHighLevelClient;
	}

	@Override
	public List<DataAsset> getDataAssets() throws IOException {
		SearchRequest searchRequest = new SearchRequest(dataAssetsIndex);
		SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
		searchSourceBuilder.query(QueryBuilders.matchAllQuery());

		searchSourceBuilder.size(limit);
		searchSourceBuilder.fetchSource(includedFields, new String[] {});
		SortOrder so = sortOrder.equalsIgnoreCase("desc") ? SortOrder.DESC : SortOrder.ASC;
		searchSourceBuilder.sort(sortBy, so);
		searchRequest.source(searchSourceBuilder);

		SearchResponse searchResponse = null;

		searchResponse = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
		SearchHits hits = searchResponse.getHits();

		SearchHit[] searchHits = hits.getHits();

		List<DataAsset> result = new ArrayList<>();
		for (SearchHit hit : searchHits) {
			Map<String, Object> sourceAsMap = hit.getSourceAsMap();

			ObjectMapper mapper = new ObjectMapper();
			mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
			DataAsset dataAsset = mapper.convertValue(sourceAsMap, DataAsset.class);
			dataAsset.setId(hit.getId());
			result.add(dataAsset);
		}

		return result;
	}

	@Override
	public DataAsset getDataAssetById(String id) throws IOException {
		GetRequest getRequest = new GetRequest(dataAssetsIndex, "_doc", id);
		FetchSourceContext fetchSourceContext = new FetchSourceContext(true, includedFields, new String[] {});
		getRequest.fetchSourceContext(fetchSourceContext);
		GetResponse getResponse = restHighLevelClient.get(getRequest, RequestOptions.DEFAULT);
		if(!getResponse.isExists()) {
			return null;
		}

		Map<String, Object> sourceMap = getResponse.getSourceAsMap();
		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

		return mapper.convertValue(sourceMap, DataAsset.class);
	}

	@Override
	public String updateProject(DataAsset dataAsset) throws IOException {
		UpdateRequest updateRequest = new UpdateRequest(dataAssetsIndex, "_doc", dataAsset.getDhId())
				.doc("dhProjects", dataAsset.getDhProjects());
		UpdateResponse updateResponse = restHighLevelClient.update(updateRequest, RequestOptions.DEFAULT);
		return updateResponse.getResult().name();
	}

	@Override
	public String removeProject(String id) throws IOException {
		List<DataAsset> dataAssets = this.getDataAssets();
		StringBuilder stringBuilder = new StringBuilder();
		for(DataAsset dataAsset: dataAssets) {
			 if (dataAsset.getDhProjects().isEmpty() || !dataAsset.getDhProjects().contains(id)) {
				 continue;
			 }
			 dataAsset.getDhProjects().remove(id);
			 stringBuilder.append(String.format("Project [%s] removed from [%s]%n", id, dataAsset.getName()));
			 this.updateProject(dataAsset);
		 }
		return stringBuilder.toString();
	}

}
