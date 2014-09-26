package com.selesse.jgitstats.git;

import com.selesse.gitwrapper.Branch;
import com.selesse.gitwrapper.GitFile;
import org.eclipse.jgit.revwalk.RevCommit;

import java.util.List;

public class BranchDetails {
    private final Branch branch;
    private final List<RevCommit> commits;
    private final List<GitFile> gitFileList;

    public BranchDetails(Branch branch, List<RevCommit> commits, List<GitFile> gitFileList) {
        this.branch = branch;
        this.commits = commits;
        this.gitFileList = gitFileList;
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
}
