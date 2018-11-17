package kr.ac.kaist.testonpr.logic;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.FileReader;

@Component
public class CoverageLogicBean {
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
