package gov.dot.its.datahub.adminapi;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import gov.dot.its.datahub.adminapi.model.DHConfiguration;
import gov.dot.its.datahub.adminapi.model.DHEngagementPopup;

public class MockDataConfiguration {

	public DHConfiguration getFakeConfiguration() {
		DHConfiguration configuration = new DHConfiguration();
		configuration.setId("default-configuration");
		configuration.setName(configuration.getId());

		List<DHEngagementPopup> engagementPopups = new ArrayList<>();
		for(int i=1; i<=2; i++) {
			DHEngagementPopup engagementPopup = this.getFakeEngagementPopup(String.valueOf(i));
			engagementPopups.add(engagementPopup);
		}

		configuration.setEngagementPopups(engagementPopups);

		return configuration;
	}

	public DHEngagementPopup getFakeEngagementPopup(String id) {
		DHEngagementPopup engagementPopup = new DHEngagementPopup();
		engagementPopup.setId(id);
		engagementPopup.setActive(false);
		engagementPopup.setContent(String.format("<h1>This is fake %s</h1>",id));
		engagementPopup.setDescription(String.format("Description %s", id));
		engagementPopup.setLastModified(new Date());
		engagementPopup.setName(String.format("EngagementPopup-%s", id));
		return engagementPopup;
	}

	public List<DHEngagementPopup> getFakeEngagementPopups() {
		List<DHEngagementPopup> result = new ArrayList<>();
		for(int i=1; i<=3; i++) {
			DHEngagementPopup popup = this.getFakeEngagementPopup(String.valueOf(i));
			result.add(popup);
		}
		return result;
	}

	public List<String> getFakeListOfImages() {
		List<String> images = new ArrayList<>();
		for(int i=1; i<=3; i++) {
			images.add(String.format("http://url.to.images/image%s.jpeg",i));
		}
		return images;
	}
}

