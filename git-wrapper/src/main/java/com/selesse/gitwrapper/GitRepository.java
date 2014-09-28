package com.selesse.gitwrapper;

import com.selesse.gitwrapper.jgit.CommitDiffs;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;

import java.io.IOException;
import java.util.List;

public class GitRepository {
    private Repository repository;

    public GitRepository(Repository repository) {
        this.repository = repository;
    }

    public Branch getBranch(String name) {
        return new Branch(repository, name);
    }

    public Repository getRepository() {
        return repository;
    }

    public List<CommitDiff> getCommitDiffs(RevCommit commit) throws IOException {
        return CommitDiffs.getCommitDiffs(this, commit);
    }
}
