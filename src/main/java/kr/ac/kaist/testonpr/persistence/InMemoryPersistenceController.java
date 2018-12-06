package kr.ac.kaist.testonpr.persistence;

import kr.ac.kaist.testonpr.model.TestSuite;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class InMemoryPersistenceController extends AbstractPersistenceController{

  Map<String, TestSuite> testSuites = new HashMap<>();
  Map<String, String> metadata = new HashMap<>();

  public void persistTestSuite(String identifier, TestSuite testSuite) {
    testSuites.put(identifier, testSuite);
  }

  public TestSuite getTestSuite(String identifier) {
    return testSuites.getOrDefault(identifier, new TestSuite());
  }

  public void storeMetadata(String key, String value) {
    metadata.put(key, value);
  }

  public String getMetadata(String key) {
    return metadata.get(key);
  }
}
