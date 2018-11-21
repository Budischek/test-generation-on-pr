package kr.ac.kaist.testonpr.logic;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import org.jacoco.core.data.*;
import org.springframework.stereotype.Component;

import java.io.*;

@Component
public class CoverageLogicBean {

    private class ProbeResult {
        boolean[] result = null;
    }

    //TODO: run individual tests instead of whole suite
    public void runTests() throws IOException {
        Process proc = Runtime.getRuntime().exec("java " +
            "-javaagent:src/main/resources/static/jacocoagent.jar=destfile=repositoryToTest/jacoco.exec  " +
            "-cp repositoryToTest/code:repositoryToTest/libs/junit-4.12.jar:repositoryToTest/libs/hamcrest-core-1.3.jar  " +
            "org.junit.runner.JUnitCore TestSuite\n");
    }

    public boolean[] getProbeActivation(String pathToExec, String classUnderTest) throws FileNotFoundException {
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
}
