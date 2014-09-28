package com.selesse.gitwrapper;

import com.google.common.base.Splitter;
import com.google.common.io.Files;
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
    private File fileInGitRoot;

    @Before
    public void setup() throws IOException {
        temporaryDirectory = Files.createTempDir();
        File gitRoot = new File(temporaryDirectory, ".git");

        boolean gitRootNewFile = gitRoot.mkdir();
        assertThat(gitRootNewFile).isTrue();

        fileInGitRoot = new File(gitRoot, "INDEX");
        boolean fileInGitRootNewFile = fileInGitRoot.createNewFile();
        assertThat(fileInGitRootNewFile).isTrue();
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
    }

    @Test
    public void testLoadRepositoryLastCommit() throws IOException, GitAPIException {
        Repository repository = SimpleGitFixture.getRepository();
        Branch branch = SimpleGitFixture.getBranch();

        List<GitFile> gitFiles = RepositoryReader.loadRepositoryLastCommit(repository, branch);
        assertThat(gitFiles).hasSize(3);

        verifyFile(gitFiles.get(0), "README.md", FileMode.REGULAR_FILE, "README\n");
        verifyFile(gitFiles.get(1), "some-file-renamed", FileMode.REGULAR_FILE, "");
        verifyFile(gitFiles.get(2), "some-other-file", FileMode.REGULAR_FILE,
                "this is some file\n" + "lol\n");
    }

    private void verifyFile(GitFile gitFile, String path, FileMode fileMode, String contents) {
        assertThat(gitFile.getPath()).isEqualTo(path);
        assertThat(gitFile.getFileMode()).isEqualTo(fileMode);

        List<String> contentList = Splitter.on("\n").splitToList(contents);

        assertThat(gitFile.getNumberOfLines()).isEqualTo(contentList.size());
        assertThat(gitFile.getContents()).isEqualTo(contentList);
    }
}