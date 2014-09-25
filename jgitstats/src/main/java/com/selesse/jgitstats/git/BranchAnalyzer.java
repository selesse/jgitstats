package com.selesse.jgitstats.git;

import com.google.common.collect.Lists;
import com.selesse.gitwrapper.Branch;
import com.selesse.gitwrapper.CommitDiff;
import com.selesse.gitwrapper.jgit.CommitDiffs;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class BranchAnalyzer {
    private static final Logger LOGGER = LoggerFactory.getLogger(BranchAnalyzer.class);

    private final File gitRoot;
    private final String branchName;

    public BranchAnalyzer(File gitRoot, String branchName) {
        this.gitRoot = gitRoot;
        this.branchName = branchName;
    }

    public BranchDetails getBranchDetails() throws IOException, GitAPIException {
        LOGGER.info("Looking for a Git repository in {}", gitRoot.getAbsolutePath());
        Repository repository = new FileRepositoryBuilder().setGitDir(gitRoot)
                .readEnvironment()
                .findGitDir()
                .build();

        LOGGER.info("Found repository with {} refs", repository.getAllRefs().size());

        Branch branch = new Branch(repository, branchName);
        List<RevCommit> commits = branch.getCommits();
        LOGGER.info("Found {} commits on {}", commits.size(), branch.getName());
        LOGGER.info("Commits: {}", commits);

        List<CommitDiff> commitDiffList = Lists.newArrayList();

        for (int i = 0; i < 1; i++) {
            RevCommit commit = commits.get(i);
            List<DiffEntry> diffEntries = CommitDiffs.getDiffs(repository, commit);

            for (DiffEntry diffEntry : diffEntries) {
                CommitDiff commitDiff = new CommitDiff(repository, diffEntry);

                commitDiffList.add(commitDiff);
            }
        }

        repository.close();

        return new BranchDetails(branch, commits, commitDiffList);
    }
}
