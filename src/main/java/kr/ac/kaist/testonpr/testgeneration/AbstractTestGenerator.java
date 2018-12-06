package kr.ac.kaist.testonpr.testgeneration;

import kr.ac.kaist.testonpr.model.TestGenerationRequest;
import kr.ac.kaist.testonpr.model.TestGenerationResponse;

public abstract class AbstractTestGenerator {
  public abstract TestGenerationResponse generateTestSuite(TestGenerationRequest request);
}
