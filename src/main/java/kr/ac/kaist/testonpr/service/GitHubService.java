package kr.ac.kaist.testonpr.service;

import org.apache.commons.io.FileUtils;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;
import org.kohsuke.github.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class GitHubService extends GitService{

  @Value("${github-oauth}")
  private String token;

  private Map<String, String> repositoriesDownloaded = new HashMap<>();
  private String DEFAULT_PATH = "repositoryToTest";

  private String TEST_REPOSITORY = "budischek/CS454";

  public String getRepositoryPath(String repoUrl) {
    return getRepositoryPath(repoUrl, DEFAULT_PATH);
  }

  public String getRepositoryPath(String repoUrl, String path) {
    if(repositoriesDownloaded.containsKey(repoUrl)) return repositoriesDownloaded.get(path);

    cloneRepository(repoUrl, path);
    repositoriesDownloaded.put(repoUrl, path);

    return path;
  }

  public void cloneRepository(String repoUrl, String path) {
    try {
      System.out.println("Deleting "+ path);
      FileUtils.deleteDirectory(new File(path));
      System.out.println("Cloning "+repoUrl+" into "+ path);
      Git.cloneRepository()
              .setURI(repoUrl)
              .setDirectory(new File(path))
              .setCredentialsProvider(
                      new UsernamePasswordCredentialsProvider("CS454-Test-on-PR",
                              "cs454testonpr")
              )
              .call();
      System.out.println("Completed Cloning");
    } catch (Exception e) {
      System.out.println("Exception occurred while cloning repo");
      e.printStackTrace();
    }
  }

  public List<String> getPullRequests() {
    try {
      GHRepository repo = getRepository(TEST_REPOSITORY);

      return repo.getPullRequests(GHIssueState.ALL).stream()
              .map(GHPullRequest::getTitle)
              .collect(Collectors.toList());
    } catch(IOException e) {
      List<String> error = new ArrayList<>();
      error.add("Error connecting to GitHub API, did you include your DevToken?");
      return error;
    }
  }

  public void reportResults(int id, String msg) {
    GHRepository repo = getRepository(TEST_REPOSITORY);

    try {
      GHPullRequest pr = repo.getPullRequest(id);

      pr.createReview()
          .commitId(pr.listCommits().asList().get(0).getSha())
          .body(msg)
          .event(GHPullRequestReviewEvent.COMMENT)
          .create();
    } catch(Exception e) {
      e.printStackTrace();
    }

  }

  //Visible for testing
  public GHRepository getRepository(String path) {
    try {

      GitHub gitHub = GitHub.connectUsingOAuth(token);

      return gitHub.getRepository(path);
    } catch(IOException e) {
        e.printStackTrace();
        return null;
    }
  }
}
