package kr.ac.kaist.testonpr.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import kr.ac.kaist.testonpr.ctgstrategy.AbstractCTGStrategy;
import kr.ac.kaist.testonpr.gitservice.AbstractGitService;
import org.jacoco.core.analysis.Analyzer;
import org.jacoco.core.analysis.CoverageBuilder;
import org.jacoco.core.data.ExecutionDataStore;
import org.jacoco.core.data.SessionInfoStore;
import org.jacoco.core.instr.Instrumenter;
import org.jacoco.core.runtime.IRuntime;
import org.jacoco.core.runtime.LoggerRuntime;
import org.jacoco.core.runtime.RuntimeData;
import org.junit.runner.JUnitCore;
import org.junit.runner.Request;
import org.junit.runner.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.tools.*;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.*;

@RestController
public class WebController {

  @Autowired
  AbstractCTGStrategy ctgStrategy;

  @Autowired
  AbstractGitService gitService;

  //Use only for development (e.g. call w/e you are currently working on)
  @RequestMapping("/dev")
  public String devController() throws Exception {
    //Process proc = Runtime.getRuntime().exec("java -javaagent:src/main/resources/static/jacocoagent.jar=destfile=repositoryToTest/jacoco.exec  -cp repositoryToTest/code:repositoryToTest/libs/junit-4.12.jar:repositoryToTest/libs/hamcrest-core-1.3.jar  org.junit.runner.JUnitCore TestSuite\n");

    /*coverageLogicBean.runTests("0"); // run the TestSuite. Generates jacoco.exec

    Thread.sleep(2000);
    boolean[] result = coverageLogicBean.getProbeActivation("repositoryToTest/jacoco.exec", "Class0");

    System.out.printf("Probes generated: %d\n", result.length);
    System.out.printf("Number of hits: %d\n", coverageLogicBean.getHitCount(result));
    */

    ctgStrategy.newPRTrigger("testTrigger");
    return "OK";
  }

  @RequestMapping("prWebhook")
  public String prWebhook(@RequestBody String payload) throws IOException {
    Map<?, ?> payloadMap = new ObjectMapper().readValue(payload, Map.class);
    Map<?,?> pr = (Map<?, ?>) payloadMap.get("pull_request");

    Integer prId = (Integer)pr.get("number");

    System.out.println("Commenting on PullRequest #" + prId);
    gitService.reportResults(prId, "Test");
    return "OK";
  }

  @Deprecated
  public void jacocoJavaAPI() throws Exception {
    final String targetName ="Class0";
    // For instrumentation and runtime we need a IRuntime instance
    // to collect execution data:
    final IRuntime runtime = new LoggerRuntime();

    // The Instrumenter creates a modified version of our test target class
    // that contains additional probes for execution data recording:
    final Instrumenter instr = new Instrumenter(runtime);
    InputStream original = getTargetClass(targetName).getResourceAsStream(targetName + ".class");
    final byte[] instrumented = instr.instrument(original, targetName);
    original.close();

    // Now we're ready to run our instrumented class and need to startup the
    // runtime first:
    final RuntimeData data = new RuntimeData();
    runtime.startup(data);

    // In this tutorial we use a special class loader to directly load the
    // instrumented class definition from a byte[] instances.
    /*final MemoryClassLoader memoryClassLoader = new MemoryClassLoader();
    memoryClassLoader.addDefinition(targetName, instrumented);
    final Class<?> targetClass = memoryClassLoader.loadClass(targetName);*/

    final Class targetClass = getTargetClass(targetName);
    // Here we execute our test target class through its Runnable interface:
    final Runnable targetInstance = (Runnable) targetClass.newInstance();
    targetInstance.run();

    // At the end of test execution we collect execution data and shutdown
    // the runtime:
    final ExecutionDataStore executionData = new ExecutionDataStore();
    final SessionInfoStore sessionInfos = new SessionInfoStore();
    data.collect(executionData, sessionInfos, false);
    runtime.shutdown();

    // Together with the original class definition we can calculate coverage
    // information:
    final CoverageBuilder coverageBuilder = new CoverageBuilder();
    final Analyzer analyzer = new Analyzer(executionData, coverageBuilder);
    original = getTargetClass(targetName).getResourceAsStream(targetName + ".class");
    analyzer.analyzeClass(original, targetName);
    original.close();
  }

  @Deprecated
  public static Class getTargetClass(String name) {
    final File file = new File("repositoryToTest/code");

    try {
      URL url = file.toURI().toURL();
      URL[] urls = new URL[]{url};
      ClassLoader cl = new URLClassLoader(urls);
      List<Class> clazzes = new ArrayList<>();

      return cl.loadClass(name);
    } catch(Exception e) {

    }
    return null;
  }

  @Deprecated
  public static String runJUnitSuite() {
    final File file = new File("repositoryToTest/code");

    try {
      URL url = file.toURI().toURL();

      URL[] urls = new URL[]{url};

      ClassLoader cl = new URLClassLoader(urls);

      List<Class> clazzes = new ArrayList<>();

      clazzes.add(cl.loadClass("Class0"));
      clazzes.add(cl.loadClass("Class1"));
      clazzes.add(cl.loadClass("TestSuite"));

      Request rq = Request.classes(clazzes.get(0), clazzes.get(1), clazzes.get(2));

      JUnitCore jUnitCore = new JUnitCore();
      Result result = jUnitCore.run(rq);

      return "OK";
    } catch(Exception e) {

    }

    return "notok";
  }
  public static String compileToClass() {
    final JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();

    final DiagnosticCollector<JavaFileObject> diagnostics = new DiagnosticCollector<>();
    final StandardJavaFileManager manager = compiler.getStandardFileManager(
        diagnostics, null, null );

    final File file = new File("repositoryToTest/code/TestClass.java");

    final Iterable< ? extends JavaFileObject > sources =
        manager.getJavaFileObjectsFromFiles( Arrays.asList( file ) );

    final List<String> options = new ArrayList<>();
    options.add("-classpath");
    System.out.println(System.getProperty("java.class.path"));
    options.add("/home/david/Dev/test-on-pr/repositoryToTest/code/");
    final List<String> classes = new ArrayList<>();
    classes.add("TestClass");
    final JavaCompiler.CompilationTask task = compiler.getTask( null, manager, diagnostics,
        options, null, sources );
    task.call();

    String ret = "";
    for(Diagnostic<? extends JavaFileObject> d : diagnostics.getDiagnostics()) {
      ret += d.getMessage(Locale.ENGLISH) + "\n";
    }

    return ret;
  }
}
