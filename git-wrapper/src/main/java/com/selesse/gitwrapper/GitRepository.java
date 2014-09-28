package com.selesse.gitwrapper;

import org.eclipse.jgit.lib.Repository;

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
}
