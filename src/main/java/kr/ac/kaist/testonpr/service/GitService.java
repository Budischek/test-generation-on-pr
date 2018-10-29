package kr.ac.kaist.testonpr.service;

import java.io.File;
import java.io.IOException;

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

  public void apiTest() {
    try {
      GitHub gitHub = GitHub.connect();

      GHRepository repo = gitHub.getRepository("test-generation-on-pr");

      repo.getPullRequests(GHIssueState.ALL).forEach(pr -> System.out.println(pr.getTitle()));
    } catch(IOException e) {
      System.out.println("Error connecting to GitHub API, did you include your DevToken?");
      e.printStackTrace();
    }
  }

}
