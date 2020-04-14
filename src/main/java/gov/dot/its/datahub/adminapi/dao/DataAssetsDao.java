package gov.dot.its.datahub.adminapi.dao;

import java.io.IOException;
import java.util.List;

import gov.dot.its.datahub.adminapi.model.DataAsset;

public interface DataAssetsDao {

	List<DataAsset> getDataAssets() throws IOException;

	DataAsset getDataAssetById(String id) throws IOException;

	String updateProject(DataAsset dataAsset) throws IOException;

	String removeProject(String id) throws IOException;

}
