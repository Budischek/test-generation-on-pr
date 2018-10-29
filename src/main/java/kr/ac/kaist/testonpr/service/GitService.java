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
import org.springframework.stereotype.Service;

@Service
public class GitService {

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
      GitHub gitHub = GitHub.connect();

      GHRepository repo = gitHub.getRepository("budischek/test-generation-on-pr");

      return repo.getPullRequests(GHIssueState.ALL).stream()
              .map(pr -> pr.getTitle())
              .collect(Collectors.toList());
    } catch(IOException e) {
      List<String> error = new ArrayList<>();
      error.add("Error connecting to GitHub API, did you include your DevToken?");
      return error;
    }
  }

}
