package kr.ac.kaist.testonpr.controller;

import kr.ac.kaist.testonpr.logic.CoverageLogicBean;
import kr.ac.kaist.testonpr.service.GitService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;

@RestController
public class GitController {

  @Autowired
  GitService gitService;

  @Autowired
  CoverageLogicBean coverageLogicBean;

  @RequestMapping("/clone")
  public String cloneRepository() {
    String repoUrl = "https://github.com/budischek/CS454.git";
    String path = "repositoryToTest";

    gitService.cloneRepository(repoUrl, path);

    return "success";
  }

  @RequestMapping("/prs")
  public List<String> getPullRequests() {
    return gitService.getPullRequests();
  }

  @RequestMapping("/addCoverageCheck")
  public String checkCoverage() throws IOException {
    return coverageLogicBean.addCheckpoint("repositoryToTest/code/Program0.java", 4);
  }

  @RequestMapping("prWebhook")
  public void prWebhook(String text) {
    System.out.println(text);
    System.out.println("###################");
  }
}
