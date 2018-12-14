package kr.ac.kaist.testonpr.ctgstrategy;

import org.springframework.beans.factory.annotation.Value;

public abstract class AbstractCTGStrategy {

  @Value("${repo-url}")
  String repoUrl;

  public abstract void newPRTrigger(String identifier);
}
