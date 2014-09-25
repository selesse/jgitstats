package com.selesse.jgitstats.git;

import com.selesse.gitwrapper.Branch;
import com.selesse.gitwrapper.CommitDiff;
import org.eclipse.jgit.revwalk.RevCommit;

import java.util.List;

public class BranchDetails {
    private final Branch branch;
    private final List<RevCommit> commits;
    private final List<CommitDiff> commitDiffList;

    public BranchDetails(Branch branch, List<RevCommit> commits, List<CommitDiff> commitDiffList) {
        this.branch = branch;
        this.commits = commits;
        this.commitDiffList = commitDiffList;
    }

    public List<CommitDiff> getCommitDiffList() {
        return commitDiffList;
    }

    public List<RevCommit> getCommits() {
        return commits;
    }

    public Branch getBranch() {
        return branch;
    }
}
