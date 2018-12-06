package kr.ac.kaist.testonpr.ctgstrategy;

import kr.ac.kaist.testonpr.gitservice.AbstractGitService;
import org.jacoco.core.data.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

@Component
public class SimpleSeedingCTGStrategy extends AbstractCTGStrategy {

  @Autowired
  AbstractGitService gitService;

  @Override
  public void newPRTrigger(String identifier) {
    String repoPath = gitService.getRepositoryPath(repoUrl);

    try {
      Process buildProcess = buildProject(repoPath);
      buildProcess.waitFor();

      Process testProcess = runTests("0"); // run the TestSuite. Generates jacoco.exec
      testProcess.waitFor();

      boolean[] result = getProbeActivation(repoPath + "/jacoco.exec", "Class0");

      System.out.printf("Probes generated: %d\n", result.length);
      System.out.printf("Number of hits: %d\n", getHitCount(result));
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public Process runTests(String tc) throws IOException {
    return Runtime.getRuntime().exec("java " +
        "-javaagent:src/main/resources/static/jacocoagent.jar=destfile=repositoryToTest/jacoco.exec  " +
        "-cp repositoryToTest/code:repositoryToTest/libs/junit-4.8.2.jar:repositoryToTest/libs/hamcrest-core-1.3.jar  " +
        "SingleJUnitTestRunner org.junit.runner.JUnitCore TestSuite#testCase" + tc + "\n");
  }

  public Process buildProject(String repoPath) throws IOException {
    ProcessBuilder pb = new ProcessBuilder("./build");
    pb.inheritIO();
    pb.directory(new File(repoPath));
    return pb.start();
  }

  private boolean[] getProbeActivation(String pathToExec, String classUnderTest) throws FileNotFoundException {
    final FileInputStream in = new FileInputStream(pathToExec);
    final ExecutionDataReader reader = new ExecutionDataReader(in);

    final ProbeResult result = new ProbeResult();
    reader.setSessionInfoVisitor(new ISessionInfoVisitor() {
      public void visitSessionInfo(final SessionInfo info) {
        return;
      }
    });
    reader.setExecutionDataVisitor(new IExecutionDataVisitor() {
      public void visitClassExecution(final ExecutionData data) {
        if(!data.getName().equals(classUnderTest)) return;

        result.result = data.getProbes();
      }
    });

    try {
      reader.read();
    } catch (Exception e) {
      e.printStackTrace();
    }

    return result.result;
  }

  private int getHitCount(final boolean[] data) {
    int count = 0;
    for (final boolean hit : data) {
      if (hit) {
        count++;
      }
    }
    return count;
  }

  private class ProbeResult {
    boolean[] result = null;
  }
}
