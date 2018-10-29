package kr.ac.kaist.testonpr;

import java.io.File;
import java.io.IOException;

import org.apache.tomcat.util.http.fileupload.FileUtils;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import kr.ac.kaist.testonpr.service.GitService;

@RunWith(SpringRunner.class)
@SpringBootTest
public class GitServiceTests {

  @Autowired
  GitService gitService;

  @Rule
  public TemporaryFolder folder= new TemporaryFolder();

  @Test
  public void testCloneRepository() throws IOException{
    Assert.assertTrue(false);
    String repoUrl = "https://github.com/trein/dev-best-practices";
    String path = folder.newFolder("tmp").getPath();

    gitService.cloneRepository(repoUrl, path);

    File file = new File(path + "README.md");

    Assert.assertNotNull(file);
  }
}
