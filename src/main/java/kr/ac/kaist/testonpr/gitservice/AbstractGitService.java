package kr.ac.kaist.testonpr.gitservice;

import java.util.List;

public abstract class AbstractGitService {

  public abstract String getRepositoryPath(String repoUrl);

  public abstract List<String> getPullRequests();

  public abstract void reportResults(int id, String msg);
}
