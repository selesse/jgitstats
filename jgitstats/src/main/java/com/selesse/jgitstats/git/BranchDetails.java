package com.selesse.jgitstats.git;

import com.google.common.base.Supplier;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;
import com.selesse.gitwrapper.*;
import org.eclipse.jgit.revwalk.RevCommit;
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
    private final List<RevCommit> commits;
    private final List<GitFile> gitFileList;
    private long totalLinesAdded;
    private long totalLinesRemoved;
    private Multimap<Author, RevCommit> authorToCommitMap;
    private Multimap<Author, CommitDiff> authorToCommitDiffMap;

    public BranchDetails(GitRepository repository, Branch branch, List<RevCommit> commits, List<GitFile> gitFileList) {
        this.repository = repository;
        this.branch = branch;
        this.commits = commits;
        this.gitFileList = gitFileList;

        Map<Author, Collection<RevCommit>> authorToCommitTreeMap = Maps.newTreeMap(getAuthorComparator());
        Map<Author, Collection<CommitDiff>> authorToCommitDiffTreeMap = Maps.newTreeMap(getAuthorComparator());

        this.authorToCommitMap = Multimaps.newListMultimap(
                authorToCommitTreeMap,
                new Supplier<List<RevCommit>>() {
                    @Override
                    public List<RevCommit> get() {
                        return Lists.newArrayList();
                    }
                }
        );
        this.authorToCommitDiffMap = Multimaps.newListMultimap(
                authorToCommitDiffTreeMap,
                new Supplier<List<CommitDiff>>() {
                    @Override
                    public List<CommitDiff> get() {
                        return Lists.newArrayList();
                    }
                }
        );

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

    public long getTotalLinesRemoved() {
        return totalLinesRemoved;
    }

    public Multimap<Author, RevCommit> getAuthorToCommitMap() {
        return authorToCommitMap;
    }

    public Multimap<Author, CommitDiff> getAuthorToCommitDiffsMap() {
        return authorToCommitDiffMap;
    }

    private void computeMembers(List<RevCommit> commits) {
        for (RevCommit revCommit : commits) {
            try {
                Author author = new Author(revCommit.getAuthorIdent());
                authorToCommitMap.put(author, revCommit);

                List<CommitDiff> diffs = repository.getCommitDiffs(revCommit);
                for (CommitDiff diff : diffs) {
                    totalLinesAdded += diff.getLinesAdded();
                    totalLinesRemoved += diff.getLinesRemoved();

                    authorToCommitDiffMap.put(author, diff);
                }
            } catch (IOException e) {
                LOGGER.error("Error getting diffs for repository {} and commit {}", repository, revCommit);
            }
        }
    }

    private Comparator<Author> getAuthorComparator() {
        return new Comparator<Author>() {
            @Override
            public int compare(Author o1, Author o2) {
                int name = o1.getName().compareToIgnoreCase(o2.getName());

                if (name == 0) {
                    return o1.getEmailAddress().compareToIgnoreCase(o2.getEmailAddress());
                }

                return name;
            }
        };
    }
}
