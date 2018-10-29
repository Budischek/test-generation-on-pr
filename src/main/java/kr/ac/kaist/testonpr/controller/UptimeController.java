package kr.ac.kaist.testonpr.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import kr.ac.kaist.testonpr.logic.UptimeLogicBean;
import kr.ac.kaist.testonpr.service.GitService;

@RestController
public class UptimeController {

  @Autowired
  UptimeLogicBean uptimeLogicBean;

  @Autowired
  GitService gitService;

  @GetMapping("/test")
  public String cloneRepository() {
    String repoUrl = "https://github.com/trein/dev-best-practices";
    String path = "repositoryToTest";

    gitService.cloneRepository(repoUrl, path);

    return "success";
  }

  @GetMapping("/uptime")
  public long getUptime() {
    return uptimeLogicBean.getUptime();
  }
}
