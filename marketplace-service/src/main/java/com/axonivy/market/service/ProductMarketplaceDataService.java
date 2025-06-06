package com.axonivy.market.service;

import com.axonivy.market.bo.VersionDownload;
import com.axonivy.market.entity.ProductMarketplaceData;
import com.axonivy.market.model.ProductCustomSortRequest;

public interface ProductMarketplaceDataService {
  void addCustomSortProduct(ProductCustomSortRequest customSort);

  int updateInstallationCountForProduct(String id, String designerVersion);

  int updateProductInstallationCount(String id);

  ProductMarketplaceData getProductMarketplaceData(String id);

  Integer getInstallationCount(String id);

  VersionDownload downloadArtifact(String artifactUrl, String productId);

  VersionDownload getVersionDownload(String productId, byte[] fileData);
}
