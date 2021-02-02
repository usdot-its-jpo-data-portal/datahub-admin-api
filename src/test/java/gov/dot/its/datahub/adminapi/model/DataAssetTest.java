package gov.dot.its.datahub.adminapi.model;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

public class DataAssetTest {
    
    @Test
	public void testInstance() {
		DataAsset dataAsset = new DataAsset();
		assertNotNull(dataAsset);
	}
}
