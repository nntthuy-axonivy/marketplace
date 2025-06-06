package com.axonivy.market;

import jakarta.persistence.EntityManager;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@ActiveProfiles("test")
public class BaseRepoSetup {
  private final String POSTGRES_DB_VENDOR_NAME= "PostgreSQL";
  @Autowired
  protected EntityManager entityManager;

  protected boolean isTableExists(String tableName) {
    String query = buildTableNameCheckQuery();
    Number count = (Number) entityManager.createNativeQuery(query).setParameter("tableName",
            tableName.toLowerCase()) // postgres table names are usually lower-case
        .getSingleResult();
    return count != null && count.intValue() > 0;
  }

  private String buildTableNameCheckQuery() {
    String databaseProductName;
    try {
      databaseProductName = entityManager.unwrap(java.sql.Connection.class).getMetaData().getDatabaseProductName();
    } catch (Exception e) {
      databaseProductName = StringUtils.EMPTY;
    }

    String query;
    if (POSTGRES_DB_VENDOR_NAME.equalsIgnoreCase(databaseProductName)) {
      query = "SELECT COUNT(*) FROM pg_tables WHERE tablename = :tableName AND schemaname = 'public'";
    } else {
      // fallback to H2 query or others
      query = "SELECT COUNT(*) FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_NAME = :tableName";
    }
    return query;
  }
}
