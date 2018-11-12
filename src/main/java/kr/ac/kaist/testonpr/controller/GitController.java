package kr.ac.kaist.testonpr.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import kr.ac.kaist.testonpr.service.GitService;

@RestController
public class GitController {

  @Autowired
  GitService gitService;

  @RequestMapping("/clone")
  public String cloneRepository() {
    String repoUrl = "github.com:lolcodez/CS454.git";
    String path = "repositoryToTest";

    gitService.cloneRepository(repoUrl, path);

    return "success";
  }

  @RequestMapping("/prs")
  public List<String> getPullRequests() {
    return gitService.getPullRequests();
  }
}
