package com.selesse.jgitstats.git;

import com.selesse.gitwrapper.Branch;
import com.selesse.gitwrapper.CommitDiff;
import com.selesse.gitwrapper.GitFile;
import com.selesse.gitwrapper.jgit.CommitDiffs;
import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;

public class BranchDetails {
    private static final Logger LOGGER = LoggerFactory.getLogger(BranchDetails.class);

    private Repository repository;
    private final Branch branch;
    private final List<RevCommit> commits;
    private final List<GitFile> gitFileList;
    private long totalLinesAdded;
    private long totalLinesRemoved;

    public BranchDetails(Repository repository, Branch branch, List<RevCommit> commits, List<GitFile> gitFileList) {
        this.repository = repository;
        this.branch = branch;
        this.commits = commits;
        this.gitFileList = gitFileList;

        calculateTotalLinesChanged(commits);
    }

    public List<RevCommit> getCommits() {
        return commits;
    }

    public Branch getBranch() {
        return branch;
    }

    public List<GitFile> getGitFileList() {
        return gitFileList;
    }

    public long getTotalNumberOfLines() {
        long totalNumberOfLines = 0;

        for (GitFile gitFile : gitFileList) {
            if (!gitFile.isBinary()) {
                totalNumberOfLines += gitFile.getNumberOfLines();
            }
        }

        return totalNumberOfLines;
    }

    public long getTotalLinesAdded() {
        return totalLinesAdded;
    }

    public long getTotalLinesRemoved() {
        return totalLinesRemoved;
    }

    private void calculateTotalLinesChanged(List<RevCommit> commits) {
        for (RevCommit revCommit : commits) {
            try {
                List<DiffEntry> diffs = CommitDiffs.getDiffs(repository, revCommit);
                for (DiffEntry diff : diffs) {
                    CommitDiff commitDiff = new CommitDiff(repository, diff);

                    LOGGER.debug("{}: +{}, -{}",
                            commitDiff.getNewPath(), commitDiff.getLinesAdded(), commitDiff.getLinesRemoved());
                    totalLinesAdded += commitDiff.getLinesAdded();
                    totalLinesRemoved += commitDiff.getLinesRemoved();
                }
            } catch (IOException e) {
                LOGGER.error("Error getting diffs for repository {} and commit {}", repository, revCommit);
            }
        }

    }
}
