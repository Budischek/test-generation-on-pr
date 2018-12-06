package kr.ac.kaist.testonpr.ctgstrategy;

import kr.ac.kaist.testonpr.gitservice.AbstractGitService;
import kr.ac.kaist.testonpr.model.ProbeStatistic;
import kr.ac.kaist.testonpr.persistence.AbstractPersistenceController;
import org.jacoco.core.data.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

@Component
public class SimpleSeedingCTGStrategy extends AbstractCTGStrategy {

  @Autowired
  AbstractGitService gitService;

  @Autowired
  AbstractPersistenceController persistenceController;

  @Override
  public void newPRTrigger(String identifier) {
    System.out.println("Downloading Repository...");
    String repoPath = gitService.getRepositoryPath(repoUrl);
    System.out.println("Download Done");

    try {
      System.out.println("Building...");
      Process buildProcess = buildProject(repoPath);
      buildProcess.waitFor();
      System.out.println("Build Done");

      System.out.println("Running Tests...");

      ProbeStatistic previousProbeResult = getPreviousResult();

      ProbeStatistic currentProbeResult = getProbeResult();
      System.out.println("Tests Done");

      compareProbeActivations(previousProbeResult, currentProbeResult);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  int amountTC     = 16;
  int amountProbes = 101;

  private ProbeStatistic getProbeResult(){
    boolean[] probeResult;

    boolean[][] suiteResult = new boolean[amountTC][amountProbes];

    for(int i=0; i < amountTC; i++) {
      try {
        Process testProcess = runTests(Integer.toString(i)); // Runs one Test Case. Generates jacoco.exec
        testProcess.waitFor();
      } catch(Exception e) {
        e.printStackTrace();
      }


      String cut = "Class0";
      if(i >= amountTC/2)
        cut = "Class1";

      probeResult = getProbeActivation("repositoryToTest/jacoco.exec", cut);

      for(int j=0; j < amountProbes; j++) {
        suiteResult[i][j] = probeResult[j];
      }
    }

    return new ProbeStatistic(suiteResult);
  }

  public void compareProbeActivations(ProbeStatistic previous, ProbeStatistic current) throws Exception {
    for(int i = 0; i < amountTC; i++) {
      for(int j=0; j < amountProbes; j++) {
        if (previous.activation[i][j] != current.activation[i][j]) {
          System.out.printf("* testCase%d reaches modified code at previousProbeResult[%d][%d] *\n", i, i, j);
          // Store the test case for seeding
        }
      }
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

  private boolean[] getProbeActivation(String pathToExec, String classUnderTest) {
    FileInputStream in = null;
    try{
      in = new FileInputStream(pathToExec);
    }
    catch (Exception e) {
      e.printStackTrace();
    }
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

  private ProbeStatistic getPreviousResult() {
    mockpreviousResult();
    ProbeStatistic previousProbeResult = new ProbeStatistic();
    previousProbeResult.deserialize(persistenceController.getMetadata("lastProbeActivation"));
    return previousProbeResult;
  }
  private void mockpreviousResult() {
    ProbeStatistic currentProbeResult = getProbeResult();

    currentProbeResult.activation[2][5] = !currentProbeResult.activation[2][5];

    String serialized = currentProbeResult.serialize();
    persistenceController.storeMetadata("lastProbeActivation", serialized);
  }
}
