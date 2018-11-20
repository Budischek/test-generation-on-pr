package kr.ac.kaist.testonpr.logic;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import org.jacoco.core.data.*;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;

@Component
public class CoverageLogicBean {

    private class ProbeResult {
        boolean[] result = null;
    }

    public boolean[] getProbeActivation(String pathToExec) throws FileNotFoundException {
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
                if(!data.getName().equals("Class1")) return;

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
