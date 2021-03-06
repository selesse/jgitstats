package com.selesse.jgitstats.git;

import com.google.common.collect.*;
import com.selesse.gitwrapper.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

public class BranchDetails {
    private static final Logger LOGGER = LoggerFactory.getLogger(BranchDetails.class);

    private GitRepository repository;
    private final Branch branch;
    private final List<Commit> commits;
    private final List<GitFile> gitFileList;
    private long totalLinesAdded;
    private long totalLinesRemoved;
    private Multimap<Author, Commit> authorToCommitMap;
    private Multimap<Author, CommitDiff> authorToCommitDiffMap;

    public BranchDetails(GitRepository repository, Branch branch, List<Commit> commits, List<GitFile> gitFileList) {
        this.repository = repository;
        this.branch = branch;
        this.commits = commits;
        this.gitFileList = gitFileList;

        Map<Author, Collection<Commit>> authorToCommitTreeMap = Maps.newTreeMap(getAuthorComparator());
        Map<Author, Collection<CommitDiff>> authorToCommitDiffTreeMap = Maps.newTreeMap(getAuthorComparator());

        this.authorToCommitMap = Multimaps.newListMultimap(authorToCommitTreeMap, Lists::newArrayList);
        this.authorToCommitDiffMap = Multimaps.newListMultimap(authorToCommitDiffTreeMap, Lists::newArrayList);

        computeMembers(commits);
    }

    public List<Commit> getCommits() {
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

    public Multimap<Author, Commit> getAuthorToCommitMap() {
        return authorToCommitMap;
    }

    public Multimap<Author, CommitDiff> getAuthorToCommitDiffsMap() {
        return authorToCommitDiffMap;
    }

    private void computeMembers(List<Commit> commits) {
        for (Commit commit : commits) {
            try {
                Author author = commit.getAuthor();
                authorToCommitMap.put(author, commit);

                List<CommitDiff> diffs = repository.getCommitDiffs(commit);
                for (CommitDiff diff : diffs) {
                    totalLinesAdded += diff.getLinesAdded();
                    totalLinesRemoved += diff.getLinesRemoved();

                    authorToCommitDiffMap.put(author, diff);
                }
            }
            catch (IOException e) {
                LOGGER.error("Error getting diffs for repository {} and commit {}", repository, commit);
            }
        }
    }

    private Comparator<Author> getAuthorComparator() {
        return (o1, o2) -> ComparisonChain.start()
                .compare(o1.getName(), o2.getName())
                .compare(o1.getEmailAddress(), o2.getEmailAddress())
                .result();
    }
}
