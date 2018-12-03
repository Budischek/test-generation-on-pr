package kr.ac.kaist.testonpr;

import kr.ac.kaist.testonpr.service.GitHubService;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.kohsuke.github.GHRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.File;
import java.io.IOException;

@RunWith(SpringRunner.class)
@SpringBootTest
public class GitHubServiceTests {

  @Autowired
  GitHubService gitService;

  private String TEST_REPOSITORY = "budischek/CS454";

  @Rule
  public TemporaryFolder folder= new TemporaryFolder();

  @Test
  public void testCloneRepository() throws IOException {
    String repoUrl = "https://github.com/trein/dev-best-practices";
    String path = folder.newFolder("tmp").getPath();

    gitService.cloneRepository(repoUrl, path);

    File file = new File(path + "README.md");

    Assert.assertNotNull(file);
  }

  @Test
  public void testGitHubAPI() throws IOException{
    GHRepository repo = gitService.getRepositoryPath(TEST_REPOSITORY);

    Assert.assertNotNull(repo);
  }
}
