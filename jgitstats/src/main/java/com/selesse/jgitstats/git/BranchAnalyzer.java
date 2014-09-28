package com.selesse.jgitstats.git;

import com.selesse.gitwrapper.Branch;
import com.selesse.gitwrapper.GitFile;
import com.selesse.gitwrapper.GitRepository;
import com.selesse.gitwrapper.RepositoryReader;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.revwalk.RevCommit;
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
        GitRepository repository = RepositoryReader.loadRepository(gitRoot);

        Branch branch = repository.getBranch(branchName);
        List<RevCommit> commits = branch.getCommits();
        LOGGER.info("Found {} commits on {}", commits.size(), branch.getName());

        List<GitFile> gitFileList = RepositoryReader.loadRepositoryLastCommit(repository, branch);

        return new BranchDetails(repository, branch, commits, gitFileList);
    }
}
