package kr.ac.kaist.testonpr.controller;

import kr.ac.kaist.testonpr.logic.CoverageLogicBean;
import org.jacoco.core.analysis.Analyzer;
import org.jacoco.core.analysis.CoverageBuilder;
import org.jacoco.core.data.*;
import org.jacoco.core.instr.Instrumenter;
import org.jacoco.core.runtime.IRuntime;
import org.jacoco.core.runtime.LoggerRuntime;
import org.jacoco.core.runtime.RuntimeData;
import org.junit.runner.JUnitCore;
import org.junit.runner.Request;
import org.junit.runner.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.tools.*;
import java.io.*;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.*;



@RestController
public class DevController {

  @Autowired
  CoverageLogicBean coverageLogicBean;

  //Use only for development (e.g. call w/e you are currently working on)
  @RequestMapping("/dev")
  public String devController() throws Exception {
    //Process proc = Runtime.getRuntime().exec("java -javaagent:src/main/resources/static/jacocoagent.jar=destfile=repositoryToTest/jacoco.exec  -cp repositoryToTest/code:repositoryToTest/libs/junit-4.12.jar:repositoryToTest/libs/hamcrest-core-1.3.jar  org.junit.runner.JUnitCore TestSuite\n");

    coverageLogicBean.runTests("0"); // run the TestSuite. Generates jacoco.exec

    Thread.sleep(2000);
    boolean[] result = coverageLogicBean.getProbeActivation("repositoryToTest/jacoco.exec", "Class0");

    System.out.printf("Probes generated: %d\n", result.length);
    System.out.printf("Number of hits: %d\n", coverageLogicBean.getHitCount(result));

    return "OK";
  }

  int amountTC     = 16;
  int amountProbes = 101;
  boolean[][] previousProbeResult = new boolean[amountTC][amountProbes];

  // Runs all test cases on the current CUTs and stores their probe result in 'previousProbeResult'
  @RequestMapping("/probeSetup")
  public String devController0() throws Exception {
    boolean[] probeResult;

    System.out.println("\nSetting up previous coverage..");

    for(int i=0; i < amountTC; i++) {
      coverageLogicBean.runTests(Integer.toString(i)); // Runs one Test Case. Generates jacoco.exec
      Thread.sleep(1200); // wait for runtests() to finish

      String cut = "Class0";
      if(i >= amountTC/2)
        cut = "Class1";

      probeResult = coverageLogicBean.getProbeActivation("repositoryToTest/jacoco.exec", cut);

      System.out.printf("\n%s - testCase%d\n", cut, i);
      System.out.printf("Probes generated: %d\n", probeResult.length);
      System.out.printf("Number  of  hits: %d\n", coverageLogicBean.getHitCount(probeResult));

      for(int j=0; j < amountProbes; j++) {
        previousProbeResult[i][j] = probeResult[j];
      }
    }

    return "OK";
  }

  // Change/Pull request is made. Recompile CUTs and then run /probeCompare
  // Runs all test cases on the changed CUTs. Compares the new probe results with 'previousProbeResult'
  // to determine which test cases reaches modified code.
  @RequestMapping("/probeCompare")
  public String devController1() throws Exception {
    boolean[] probeResult;

    System.out.printf("\nRunning %d test cases and comparing with previous coverage:\n", amountTC);

    for(int i=0; i < amountTC; i++) {
      coverageLogicBean.runTests(Integer.toString(i)); // Runs one Test Case. Generates jacoco.exec
      Thread.sleep(1200); // wait for runtests() to finish

      String cut = "Class0";
      if(i >= amountTC/2)
        cut = "Class1";

      probeResult = coverageLogicBean.getProbeActivation("repositoryToTest/jacoco.exec", cut);

      System.out.printf("\n%s - testCase%d\n", cut, i);
      System.out.printf("Probes generated: %d\n", probeResult.length);
      System.out.printf("Number  of  hits: %d\n", coverageLogicBean.getHitCount(probeResult));

      for(int j=0; j < amountProbes; j++) {
        if(previousProbeResult[i][j] != probeResult[j]) {
          System.out.printf("* testCase%d reaches modified code at previousProbeResult[%d][%d] *\n", i, i, j);
          // Store the test case for seeding
        }
      }
    }

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

  public static void evosuiteSetUpCommand(){
    try {
      // Run Windows command
      Runtime rt = Runtime.getRuntime();
      Process proc1 = rt.exec("cd repositoryToTest");
     
      Process proc = rt.exec(" set EVOSUITE=java -jar \"%CD%\"\\evosuite-1.0.6.jar ");
			// Process proc = rt.exec(" export EVOSUITE=\"java -jar $(pwd)/evosuite-1.0.6.jar\" ");
      

      // Read command errors
      System.out.println("Standard error: ");
      while ((s = stdError.readLine()) != null) {
       System.out.println(s);
      }
      
    } catch (Exception e) {
        e.printStackTrace(System.err);
    }
  } 

  public static void evosuiteGenerateTestsForClass(String packageName, String classname, String classpath){
    try{
    //%EVOSUITE% -class "packageName"."classname" -projectCP "classpath to the package"  

      Runtime rt = Runtime.getRuntime();
      Process proc = rt.exec("%EVOSUITE% -class "+packageName+"."+classname+" -projectCP "+classpath);
      
      // Read command standard output
      String s;
      System.out.println("Standard output: ");
      while ((s = stdInput.readLine()) != null) {
        System.out.println(s);
      }

      // Read command errors
      System.out.println("Standard error: ");
      while ((s = stdError.readLine()) != null) {
       System.out.println(s);
      }

    }catch (Exception e) {
        e.printStackTrace(System.err);
    }
  }


  public static void evosuiteRunTests(String classpath){
    try{

      //set the CLASSPATH environment variable, including :
      //"classpath": This is the root directory which we need for the CUTs
      // evosuite-standalone-runtime-1.0.6.jar: This is the EvoSuite runtime library.
      // evosuite-tests: This is the root directory where we put the test class files
      // junit-4.12.jar and hamcrest-core-1.3.jar: We need JUnit to execute JUnit tests.
      Runtime rt = Runtime.getRuntime();
      Process proc = rt.exec("set CLASSPATH="+classpath+";evosuite-standalone-runtime-1.0.6.jar;evosuite-tests;junit-4.12.jar;hamcrest-core-1.3.jar ");
      
      // Read command standard output
      String s;
      System.out.println("Standard output: ");
      while ((s = stdInput.readLine()) != null) {
        System.out.println(s);
      }

      // Read command errors
      System.out.println("Standard error: ");
      while ((s = stdError.readLine()) != null) {
       System.out.println(s);
      }
    
    
      //compile the tests in place
      Runtime rt = Runtime.getRuntime();
      Process proc = rt.exec("javac evosuite-tests/tutorial/*.java");
      
      // Read command standard output
      String s;
      System.out.println("Standard output: ");
      while ((s = stdInput.readLine()) != null) {
        System.out.println(s);
      }

      // Read command errors
      System.out.println("Standard error: ");
      while ((s = stdError.readLine()) != null) {
       System.out.println(s);
      }


      // //run the tests
      // Runtime rt = Runtime.getRuntime();
      // Process proc = rt.exec("java org.junit.runner.JUnitCore tutorial.*_ESTest");
      
      // // java org.junit.runner.JUnitCore tutorial.Class_ESTest
      


      // // Read command standard output
      // String s;
      // System.out.println("Standard output: ");
      // while ((s = stdInput.readLine()) != null) {
      //   System.out.println(s);
      // }

      // // Read command errors
      // System.out.println("Standard error: ");
      // while ((s = stdError.readLine()) != null) {
      //  System.out.println(s);
      // }


      

    }catch (Exception e) {
        e.printStackTrace(System.err);
    }
  }


}
