package kr.ac.kaist.testonpr.persistence;

import kr.ac.kaist.testonpr.model.TestSuite;

public abstract class AbstractPersistenceController {
  public abstract void persistTestSuite(String identifier, TestSuite testSuite);

  public abstract TestSuite getTestSuite(String identifier);

  public abstract void storeMetadata(String key, String value);

  public abstract String getMetadata(String key);
}
