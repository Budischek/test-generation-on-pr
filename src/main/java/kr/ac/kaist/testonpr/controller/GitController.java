package kr.ac.kaist.testonpr.controller;

import java.io.*;
import java.util.Iterator;
import java.util.List;

import com.github.javaparser.JavaParser;
import com.github.javaparser.JavaToken;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import kr.ac.kaist.testonpr.logic.CoverageLogicBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import kr.ac.kaist.testonpr.service.GitService;

@RestController
public class GitController {

  @Autowired
  GitService gitService;

  @Autowired
  CoverageLogicBean coverageLogicBean;

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

  @RequestMapping("/addCoverageCheck")
  public String checkCoverage() throws IOException {
    return coverageLogicBean.addCheckpoint("repositoryToTest/code/Program0.java", 4);
  }
}
