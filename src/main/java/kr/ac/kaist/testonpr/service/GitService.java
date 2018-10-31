package kr.ac.kaist.testonpr.service;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.kohsuke.github.GHIssueState;
import org.kohsuke.github.GHRepository;
import org.kohsuke.github.GitHub;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class GitService {

  @Value("${github-oauth}")
  private String token;

  public void cloneRepository(String repoUrl, String path) {
    try {
      System.out.println("Cloning "+repoUrl+" into "+ path);
      Git.cloneRepository()
              .setURI(repoUrl)
              .setDirectory(new File(path))
              .call();
      System.out.println("Completed Cloning");
    } catch (GitAPIException e) {
      System.out.println("Exception occurred while cloning repo");
      e.printStackTrace();
    }
  }

  public List<String> getPullRequests() {
    try {
      GHRepository repo = getRepository();

      return repo.getPullRequests(GHIssueState.ALL).stream()
              .map(pr -> pr.getTitle())
              .collect(Collectors.toList());
    } catch(IOException e) {
      List<String> error = new ArrayList<>();
      error.add("Error connecting to GitHub API, did you include your DevToken?");
      return error;
    }
  }

  //Visible for testing
  public GHRepository getRepository() throws IOException{
    GitHub gitHub = GitHub.connectUsingOAuth(token);

    return gitHub.getRepository("budischek/test-generation-on-pr");
  }
}
