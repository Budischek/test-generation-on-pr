package kr.ac.kaist.testonpr.logic;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import org.jacoco.core.data.*;
import org.springframework.stereotype.Component;
import java.io.*;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;



import java.io.InputStream;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.Map;

import org.jacoco.core.analysis.Analyzer;
import org.jacoco.core.analysis.CoverageBuilder;
import org.jacoco.core.analysis.IClassCoverage;
import org.jacoco.core.analysis.ICounter;
import org.jacoco.core.data.ExecutionDataStore;
import org.jacoco.core.data.SessionInfoStore;
import org.jacoco.core.instr.Instrumenter;
import org.jacoco.core.runtime.IRuntime;
import org.jacoco.core.runtime.LoggerRuntime;
import org.jacoco.core.runtime.RuntimeData;



import java.io.InputStream;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.Map;

import org.jacoco.core.analysis.Analyzer;
import org.jacoco.core.analysis.CoverageBuilder;
import org.jacoco.core.analysis.IClassCoverage;
import org.jacoco.core.analysis.ICounter;
import org.jacoco.core.data.ExecutionDataStore;
import org.jacoco.core.data.SessionInfoStore;
import org.jacoco.core.instr.Instrumenter;
import org.jacoco.core.runtime.IRuntime;
import org.jacoco.core.runtime.LoggerRuntime;
import org.jacoco.core.runtime.RuntimeData;


@Component
public class CoverageLogicBean {

    private class ProbeResult {
        boolean[] result = null;
    }

    // TODO: run individual tests instead of whole suite
    public void runTests() throws IOException {
        Process proc = Runtime.getRuntime().exec("java " +
            "-javaagent:src/main/resources/static/jacocoagent.jar=destfile=repositoryToTest/jacoco.exec  " +
            "-cp repositoryToTest/code:repositoryToTest/libs/junit-4.12.jar:repositoryToTest/libs/hamcrest-core-1.3.jar  " +
            "org.junit.runner.JUnitCore TestSuite\n");
    }

    public boolean[] getProbeActivation(String pathToExec, String classUnderTest) throws FileNotFoundException {
        final FileInputStream fin = new FileInputStream(pathToExec);
        final ExecutionDataReader reader = new ExecutionDataReader(fin);

        final ProbeResult result = new ProbeResult();
        reader.setSessionInfoVisitor(new ISessionInfoVisitor() {
            public void visitSessionInfo(final SessionInfo info) {
                return;
            }
        });
        reader.setExecutionDataVisitor(new IExecutionDataVisitor() {
            public void visitClassExecution(final ExecutionData data) {
                if(!data.getName().equals(classUnderTest)) return;
                result.result = data.getProbes(); // get probes t/f for chosen CUT
            }
        });

        try {
            reader.read();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return result.result;
    }

    public static void writeJson(boolean[] probes) throws Exception {
        JSONObject sampleObject = new JSONObject();

        JSONArray arr = new JSONArray();
        for(boolean probe:probes) {
          arr.add(probe);
        }

        sampleObject.put("probes", arr);

        try {
          FileWriter file = new FileWriter("repositoryToTest/result.json");
          file.write(sampleObject.toJSONString());
          file.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println("Probe result stored at: repositoryToTest/result.json");
    }



    public int getHitCount(final boolean[] data) {
        int count = 0;
        for (final boolean hit : data) {
            if (hit) {
                count++;
            }
        }
        return count;
    }

    @Deprecated
    public String addCheckpoint(String path, int lineNum) {
        try {
            BufferedReader reader = new BufferedReader(new FileReader(path));

            String line;
            String program = "";
            int i = 0;
            while((line = reader.readLine()) != null) {
                i++;
                if(i == lineNum) {
                    program += "System.out.println(\"test\");\n";
                }
                program += line + "\n";
            }

            CompilationUnit cu = JavaParser.parse(program);

            return cu.toString();
        } catch (Exception e) {
            return e.toString();
        }
    }
    /*******************************************************************************
 * Copyright (c) 2009, 2018 Mountainminds GmbH & Co. KG and Contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Marc R. Hoffmann - initial API and implementation
 *
 *******************************************************************************/

  /**
   * Example usage of the JaCoCo core API. In this tutorial a single target class
   * will be instrumented and executed. Finally the coverage information will be
   * dumped.
   */

  	/**
  	 * The test target we want to see code coverage for.
  	 */
  	public static class TestTarget implements Runnable {

  		public void run() {
  			isPrime(7);
  		}

  		private boolean isPrime(final int n) {
  			for (int i = 2; i * i <= n; i++) {
  				if ((n ^ i) == 0) {
  					return false;
  				}
  			}
  			return true;
  		}

  	}

  	/**
  	 * A class loader that loads classes from in-memory data.
  	 */
  	public static class MemoryClassLoader extends ClassLoader {

  		private final Map<String, byte[]> definitions = new HashMap<String, byte[]>();

  		/**
  		 * Add a in-memory representation of a class.
  		 *
  		 * @param name
  		 *            name of the class
  		 * @param bytes
  		 *            class definition
  		 */
  		public void addDefinition(final String name, final byte[] bytes) {
  			definitions.put(name, bytes);
  		}

  		@Override
  		protected Class<?> loadClass(final String name, final boolean resolve)
  				throws ClassNotFoundException {
  			final byte[] bytes = definitions.get(name);
  			if (bytes != null) {
  				return defineClass(name, bytes, 0, bytes.length);
  			}
  			return super.loadClass(name, resolve);
  		}

  	}

  	/**
  	 * Run this example.
  	 *
  	 * @throws Exception
  	 *             in case of errors
  	 */
  	public void execute() throws Exception {
  		final String targetName = TestTarget.class.getName();

  		// For instrumentation and runtime we need a IRuntime instance
  		// to collect execution data:
  		final IRuntime runtime = new LoggerRuntime();

  		// The Instrumenter creates a modified version of our test target class
  		// that contains additional probes for execution data recording:
  		final Instrumenter instr = new Instrumenter(runtime);
  		InputStream original = getTargetClass(targetName);
  		final byte[] instrumented = instr.instrument(original, targetName);
  		original.close();

  		// Now we're ready to run our instrumented class and need to startup the
  		// runtime first:
  		final RuntimeData data = new RuntimeData();
  		runtime.startup(data);

  		// In this tutorial we use a special class loader to directly load the
  		// instrumented class definition from a byte[] instances.
  		final MemoryClassLoader memoryClassLoader = new MemoryClassLoader();
  		memoryClassLoader.addDefinition(targetName, instrumented);
  		final Class<?> targetClass = memoryClassLoader.loadClass(targetName);

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
  		original = getTargetClass(targetName);
  		analyzer.analyzeClass(original, targetName);
  		original.close();

  		// Let's dump some metrics and line coverage information:
  		for (final IClassCoverage cc : coverageBuilder.getClasses()) {
  			System.out.printf("Coverage of class %s%n", cc.getName());

  			printCounter("instructions", cc.getInstructionCounter());
  			printCounter("branches", cc.getBranchCounter());
  			printCounter("lines", cc.getLineCounter());
  			printCounter("methods", cc.getMethodCounter());
  			printCounter("complexity", cc.getComplexityCounter());

  			for (int i = cc.getFirstLine(); i <= cc.getLastLine(); i++) {
  				System.out.printf("Line %s: %s%n", Integer.valueOf(i),
  						getColor(cc.getLine(i).getStatus()));
  			}
  		}
  	}

  	private InputStream getTargetClass(final String name) {
  		final String resource = '/' + name.replace('.', '/') + ".class";
  		return getClass().getResourceAsStream(resource);
  	}

  	private void printCounter(final String unit, final ICounter counter) {
  		final Integer missed = Integer.valueOf(counter.getMissedCount());
  		final Integer total = Integer.valueOf(counter.getTotalCount());
  		System.out.printf("%s of %s %s missed%n", missed, total, unit);
  	}

  	private String getColor(final int status) {
  		switch (status) {
  		case ICounter.NOT_COVERED:
  			return "red";
  		case ICounter.PARTLY_COVERED:
  			return "yellow";
  		case ICounter.FULLY_COVERED:
  			return "green";
  		}
  		return "";
  	}

  	/**
  	 * Entry point to run this examples as a Java application.
  	 *
  	 * @param args
  	 *            list of program arguments
  	 * @throws Exception
  	 *             in case of errors
  	 */
  	// public static void main(final String[] args) throws Exception {
  	// 	new CoreTutorial(System.out).execute();
  	// }
}
