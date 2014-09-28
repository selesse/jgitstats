package com.selesse.gitwrapper;

import com.google.common.base.Splitter;
import com.google.common.io.Files;
import com.selesse.gitwrapper.fixtures.GitRepositoryBuilder;
import com.selesse.gitwrapper.fixtures.SimpleGitFixture;
import org.apache.commons.io.FileUtils;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.FileMode;
import org.eclipse.jgit.lib.Repository;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.List;

import static org.fest.assertions.api.Assertions.assertThat;

public class RepositoryReaderTest {
    private File temporaryDirectory;
    private File gitRoot;
    private File fileInGitRoot;

    @Before
    public void setup() throws IOException {
        temporaryDirectory = Files.createTempDir();
        gitRoot = new File(temporaryDirectory, ".git");

        boolean gitRootNewFile = gitRoot.mkdir();
        assertThat(gitRootNewFile).isTrue();

        fileInGitRoot = new File(gitRoot, "INDEX");
        boolean fileInGitRootNewFile = fileInGitRoot.createNewFile();
        assertThat(fileInGitRootNewFile).isTrue();

        File anotherFileInGitRoot = new File(gitRoot, ".git");
        boolean anotherFileInGitRootNewFile = anotherFileInGitRoot.createNewFile();
        assertThat(anotherFileInGitRootNewFile).isTrue();
    }

    @After
    public void tearDown() throws IOException {
        FileUtils.deleteDirectory(temporaryDirectory);
    }

    @Test
    public void testIsValidGitRoot() throws IOException {
        assertThat(RepositoryReader.isValidGitRoot("")).isFalse();
        assertThat(RepositoryReader.isValidGitRoot(temporaryDirectory.getAbsolutePath())).isTrue();
        assertThat(RepositoryReader.isValidGitRoot(fileInGitRoot.getAbsolutePath())).isFalse();
        assertThat(RepositoryReader.isValidGitRoot(gitRoot.getAbsolutePath())).isFalse();
    }

    @Test
    public void testLoadRepositoryLastCommit() throws IOException, GitAPIException {
        Repository repository = SimpleGitFixture.getRepository();
        Branch branch = SimpleGitFixture.getBranch();

        List<GitFile> gitFiles = RepositoryReader.loadRepositoryLastCommit(repository, branch);
        assertThat(gitFiles).hasSize(4);

        verifyTextFile(gitFiles.get(0), "README.md", "README\n");
        verifyTextFile(gitFiles.get(1), "some-file-renamed", "");
        verifyTextFile(gitFiles.get(2), "some-other-file", "this is some file\n" + "lol\n");
        verifyExecutableBinaryFile(gitFiles.get(3), "some/binary/file/hello.o");
    }

    @Test
    public void testLoadRepository() throws IOException, InterruptedException {
        GitRepositoryBuilder repositoryBuilder = GitRepositoryBuilder.create().runCommand("git init").build();
        Repository repository = RepositoryReader.loadRepository(repositoryBuilder.getDirectory());

        assertThat(repository).isNotNull();
    }

    @Test
    public void testLoadRepository_throwsException() throws IOException {
        boolean exceptionWasThrown = false;

        try {
            RepositoryReader.loadRepository(gitRoot);
        } catch (IOException e) {
            assertThat(e).hasMessage("Invalid Git root " + gitRoot.getAbsolutePath());
            exceptionWasThrown = true;
        }

        assertThat(exceptionWasThrown).isTrue();
    }

    private void verifyExecutableBinaryFile(GitFile gitFile, String path) {
        assertThat(gitFile.getPath()).isEqualTo(path);
        assertThat(gitFile.getFileMode()).isEqualTo(FileMode.EXECUTABLE_FILE);
        assertThat(gitFile.isBinary()).isTrue();
    }

    private void verifyTextFile(GitFile gitFile, String path, String contents) {
        assertThat(gitFile.getPath()).isEqualTo(path);
        assertThat(gitFile.getFileMode()).isEqualTo(FileMode.REGULAR_FILE);
        assertThat(gitFile.isBinary()).isFalse();

        List<String> contentList = Splitter.on("\n").splitToList(contents);

        assertThat(gitFile.getNumberOfLines()).isEqualTo(contentList.size());
        assertThat(gitFile.getContents()).isEqualTo(contentList);
    }
}