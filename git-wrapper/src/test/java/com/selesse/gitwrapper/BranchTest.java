package com.selesse.gitwrapper;

import com.selesse.gitwrapper.fixtures.GitRepositoryBuilder;
import com.selesse.gitwrapper.fixtures.SimpleGitFixture;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.junit.Test;

import java.io.File;
import java.util.List;

import static org.fest.assertions.api.Assertions.assertThat;

public class BranchTest {

    @Test
    public void testGetCommits() throws Exception {
        Branch branch = SimpleGitFixture.getBranch();
        List<RevCommit> commits = branch.getCommits();

        assertThat(commits).hasSize(4);

        commits = branch.getCommits();

        assertThat(commits).hasSize(4);
    }

    @Test
    public void testGetCommits_dynamic() throws Exception {
        GitRepositoryBuilder repositoryBuilder = GitRepositoryBuilder.create().
                runCommand("git init").
                createFile("README.md", "This is a README").
                runCommand("git add README.md").
                runCommand("git commit -m Commit").
                createFile("new_file/nested", "This is a new nested file").
                runCommand("git add new_file/nested").
                runCommand("git commit -m Commit2").
                createFile("another_file", "This is another file").
                runCommand("git add another_file").
                runCommand("git commit -m Commit3").
                build();

        File directory = repositoryBuilder.getDirectory();
        Repository repository = RepositoryReader.loadRepository(directory);
        Branch branch = new Branch(repository, "master");
        List<RevCommit> commits = branch.getCommits();

        repositoryBuilder.cleanUp();

        assertThat(commits).hasSize(3);
    }

    @Test
    public void testGetCommits_onOtherBranch() throws Exception {
        GitRepositoryBuilder repositoryBuilder = GitRepositoryBuilder.create().
                runCommand("git init").
                createFile("README.md", "This is a README").
                runCommand("git add README.md").
                runCommand("git", "commit", "-m", "Initial Commit").
                runCommand("git checkout -b newBranch").
                createFile("new_file/nested", "This is a new nested file").
                runCommand("git add new_file/nested").
                runCommand("git commit -m Commit2").
                createFile("another_file", "This is another file").
                runCommand("git add another_file").
                runCommand("git commit -m Commit3").
                build();

        File directory = repositoryBuilder.getDirectory();
        Repository repository = RepositoryReader.loadRepository(directory);

        Branch masterBranch = new Branch(repository, "master");
        List<RevCommit> masterCommits = masterBranch.getCommits();

        Branch newBranch = new Branch(repository, "newBranch");
        List<RevCommit> newBranchCommits = newBranch.getCommits();

        repositoryBuilder.cleanUp();

        assertThat(masterCommits).hasSize(1);
        assertThat(newBranchCommits).hasSize(3);
    }
}
