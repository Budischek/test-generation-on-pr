package kr.ac.kaist.testonpr.service;

import java.util.List;

public abstract class GitService {

  public abstract String getRepositoryPath(String repoUrl);

  public abstract List<String> getPullRequests();

  public abstract void reportResults(int id, String msg);
}
