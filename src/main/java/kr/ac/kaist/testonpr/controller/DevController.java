package kr.ac.kaist.testonpr.controller;

import org.junit.runner.JUnitCore;
import org.junit.runner.Request;
import org.junit.runner.Result;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.tools.*;
import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

@RestController
public class DevController {

  //Use only for development (e.g. call w/e you are currently working on)
  @RequestMapping("/dev")
  public String devController() throws ClassNotFoundException{
    //Request rq = new Request.classes("Class0", "Class1", "TestClass");


    //Result result = new JunitCore().run(rq);

    //return compileToClass();
    //return "OK";

    final File file = new File("repositoryToTest/code");

    try {
      URL url = file.toURI().toURL();

      URL[] urls = new URL[]{url};

      ClassLoader cl = new URLClassLoader(urls);

      List<Class> clazzes = new ArrayList<>();

      clazzes.add(cl.loadClass("Class0"));
      clazzes.add(cl.loadClass("Class1"));
      clazzes.add(cl.loadClass("TestClass"));

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
