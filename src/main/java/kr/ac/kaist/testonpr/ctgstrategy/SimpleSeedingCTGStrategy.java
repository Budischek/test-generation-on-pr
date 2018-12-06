package kr.ac.kaist.testonpr.ctgstrategy;

import kr.ac.kaist.testonpr.gitservice.AbstractGitService;
import org.springframework.beans.factory.annotation.Autowired;

public class SimpleSeedingCTGStrategy extends AbstractCTGStrategy {

  @Autowired
  AbstractGitService gitService;

  @Override
  public void newPRTrigger(String identifier) {
    String repoPath = gitService.getRepositoryPath(repoUrl);


  }
}
