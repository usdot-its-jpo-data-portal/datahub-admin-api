package gov.dot.its.datahub.adminapi.model;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class DataAsset {
	private String id;
	private String name;
	private String description;
	private String accessLevel;
	private Timestamp lastUpdate;
	private List<String> tags;
	private String sourceUrl;
	private String dhId;
	private Timestamp dhLastUpdate;
	private String dhSourceName;
	private Map<String, List<String>> highlights;
	private Float esScore;
	private List<RelatedItemModel> related;
	private Metrics metrics;
	private List<String> dhProjects;
	private List<String> dhDataTypes;
	
	@Value("${datahub.admin.api.configurations.mask.tag}")
	private String maskTag;

	public DataAsset() {
		this.tags = new ArrayList<>();
		this.highlights = new HashMap<>();
		this.related = new ArrayList<>();
		this.dhProjects = new ArrayList<>();
		this.dhDataTypes = new ArrayList<>();
	}

	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getAccessLevel() {
		return accessLevel;
	}
	public void setAccessLevel(String accessLevel) {
		this.accessLevel = accessLevel;
	}
	public Timestamp getLastUpdate() {
		return lastUpdate;
	}
	public void setLastUpdate(Timestamp lastUpdate) {
		this.lastUpdate = lastUpdate;
	}
	public List<String> getTags() {
		return tags;
	}
	public void setTags(List<String> tags) {
		this.tags = tags;
	}
	public String getSourceUrl() {
		return sourceUrl;
	}
	public void setSourceUrl(String sourceUrl) {
		this.sourceUrl = sourceUrl;
	}
	public String getDhId() {
		return dhId;
	}
	public void setDhId(String dhId) {
		this.dhId = dhId;
	}
	public Timestamp getDhLastUpdate() {
		return dhLastUpdate;
	}
	public void setDhLastUpdate(Timestamp dhLastUpdate) {
		this.dhLastUpdate = dhLastUpdate;
	}
	public String getDhSourceName() {
		return dhSourceName;
	}
	public void setDhSourceName(String dhSourceName) {
		this.dhSourceName = dhSourceName;
	}
	public Map<String, List<String>> getHighlights() {
		return highlights;
	}
	public void setHighlights(Map<String, List<String>> highlight) {
		this.highlights = highlight;
	}
	public Float getEsScore() {
		return esScore;
	}

	public void setEsScore(Float esScore) {
		this.esScore = esScore;
	}

	public List<RelatedItemModel> getRelated() {
		return related;
	}

	public void setRelated(List<RelatedItemModel> related) {
		this.related = related;
	}

	public Metrics getMetrics() {
		return metrics;
	}

	public void setMetrics(Metrics metrics) {
		this.metrics = metrics;
	}

	public List<String> getDhProjects() {
		return dhProjects;
	}

	public void setDhProjects(List<String> dhProjects) {
		this.dhProjects = dhProjects;
	}

	public List<String> getDhDataTypes() {
		return dhDataTypes;
	}

	public void setDhDataTypes(List<String> dhDataTypes) {
		this.dhDataTypes = dhDataTypes;
	}
	
	public boolean isHidden() {
		return this.getTags().contains(this.maskTag);
	}

}
