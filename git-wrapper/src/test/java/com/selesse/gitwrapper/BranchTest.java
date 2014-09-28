package com.selesse.gitwrapper;

import com.selesse.gitwrapper.fixtures.SimpleGitFixture;
import org.eclipse.jgit.revwalk.RevCommit;
import org.junit.Test;

import java.util.List;

import static org.fest.assertions.api.Assertions.assertThat;

public class BranchTest {

    @Test
    public void testGetCommits() throws Exception {
        Branch branch = SimpleGitFixture.getBranch();
        List<RevCommit> commits = branch.getCommits();

        assertThat(commits).hasSize(4);
    }
}