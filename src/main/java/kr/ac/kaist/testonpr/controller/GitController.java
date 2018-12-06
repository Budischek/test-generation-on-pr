package kr.ac.kaist.testonpr.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import kr.ac.kaist.testonpr.logic.CoverageLogicBean;
import kr.ac.kaist.testonpr.gitservice.AbstractGitService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@RestController
public class GitController {

  @Autowired
  AbstractGitService gitService;

  @Autowired
  CoverageLogicBean coverageLogicBean;

  @RequestMapping("/clone")
  public String cloneRepository() {
    String repoUrl = "https://github.com/budischek/CS454.git";


    gitService.getRepositoryPath(repoUrl);

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
  public String prWebhook(@RequestBody String payload) throws IOException{
    Map<?, ?> payloadMap = new ObjectMapper().readValue(payload, Map.class);
    Map<?,?> pr = (Map<?, ?>) payloadMap.get("pull_request");

    Integer prId = (Integer)pr.get("number");

    System.out.println("Commenting on PullRequest #" + prId);
    gitService.reportResults(prId, "Test");
    return "OK";
  }
}
