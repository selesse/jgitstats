package com.selesse.jgitstats.git;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import com.selesse.gitwrapper.*;
import org.eclipse.jgit.revwalk.RevCommit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;

public class BranchDetails {
    private static final Logger LOGGER = LoggerFactory.getLogger(BranchDetails.class);

    private GitRepository repository;
    private final Branch branch;
    private final List<RevCommit> commits;
    private final List<GitFile> gitFileList;
    private long totalLinesAdded;
    private long totalLinesRemoved;
    private Multimap<Author, RevCommit> authorToCommitMap;

    public BranchDetails(GitRepository repository, Branch branch, List<RevCommit> commits, List<GitFile> gitFileList) {
        this.repository = repository;
        this.branch = branch;
        this.commits = commits;
        this.gitFileList = gitFileList;
        this.authorToCommitMap = ArrayListMultimap.create();

        computeMembers(commits);
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

    public Multimap<Author, RevCommit> getAuthorToCommitMap() {
        return authorToCommitMap;
    }

    public long getTotalLinesRemoved() {
        return totalLinesRemoved;
    }

    private void computeMembers(List<RevCommit> commits) {
        for (RevCommit revCommit : commits) {
            try {
                List<CommitDiff> diffs = repository.getCommitDiffs(revCommit);
                for (CommitDiff diff : diffs) {
                    totalLinesAdded += diff.getLinesAdded();
                    totalLinesRemoved += diff.getLinesRemoved();
                }

                Author author = new Author(revCommit.getAuthorIdent());
                authorToCommitMap.put(author, revCommit);
            } catch (IOException e) {
                LOGGER.error("Error getting diffs for repository {} and commit {}", repository, revCommit);
            }
        }
    }
}
