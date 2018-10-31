package kr.ac.kaist.testonpr.service;

import java.io.File;
import java.nio.file.Paths;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
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
}
