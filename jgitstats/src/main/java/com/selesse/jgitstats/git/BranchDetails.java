package com.selesse.jgitstats.git;

import com.selesse.gitwrapper.Branch;
import org.eclipse.jgit.revwalk.RevCommit;

import java.util.List;

public class BranchDetails {
    private final Branch branch;
    private final List<RevCommit> commits;

    public BranchDetails(Branch branch, List<RevCommit> commits) {
        this.branch = branch;
        this.commits = commits;
    }

    public List<RevCommit> getCommits() {
        return commits;
    }

    public Branch getBranch() {
        return branch;
    }
}
