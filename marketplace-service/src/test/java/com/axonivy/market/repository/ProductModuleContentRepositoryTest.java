package com.axonivy.market.repository;

import com.axonivy.market.BaseRepoSetup;
import com.axonivy.market.entity.ProductModuleContent;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;


public class ProductModuleContentRepositoryTest extends BaseRepoSetup {
  @Autowired
  private ProductModuleContentRepository repo;
  private final String MOCK_PRODUCT_ID = "portal";
  private final String MOCK_PRODUCT_MODULE_CONTENT_ID = "1";
  private final String MOCK_TABLE_NAME = "metadata";
  private final String MOCK_VERSION = "10.0.27";

  @Test
  @Transactional
  public void testSqlInjectionAttemptOnArtifactId() {
    assertTrue(repo.findByVersionAndProductId(MOCK_VERSION, MOCK_PRODUCT_ID) == null);

    ProductModuleContent mockData = new ProductModuleContent();
    mockData.setProductId(MOCK_PRODUCT_ID);
    mockData.setId(MOCK_PRODUCT_MODULE_CONTENT_ID);
    mockData.setVersion(MOCK_VERSION);
    repo.save(mockData);

    assertTrue(repo.findByVersionAndProductId(MOCK_VERSION, MOCK_PRODUCT_ID) != null);
    assertTrue(isTableExists(MOCK_TABLE_NAME), "Table metadata is not exist");

    String maliciousProductId = "portal; DROP TABLE metadata;";
    repo.deleteAllByProductId(maliciousProductId);
    // Verify the malicious id is handled as text
    assertTrue(repo.findByVersionAndProductId(MOCK_VERSION, MOCK_PRODUCT_ID) != null);
    assertTrue(isTableExists(MOCK_TABLE_NAME), "Table metadata is not exist");


    // Test if malicious id is not handled - treated by sql command
    entityManager.createNativeQuery(
            "DELETE FROM product_module_content WHERE product_id = '" + "portal'; DROP TABLE metadata;")
        .executeUpdate();
    assertTrue(repo.findByVersionAndProductId(MOCK_VERSION, MOCK_PRODUCT_ID) == null);
    assertFalse(isTableExists(MOCK_TABLE_NAME), "Table metadata is not cleared");
  }
}
