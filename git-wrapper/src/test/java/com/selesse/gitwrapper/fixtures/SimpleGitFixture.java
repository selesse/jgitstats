package com.selesse.gitwrapper.fixtures;

import com.google.common.io.Resources;
import com.selesse.gitwrapper.Branch;
import com.selesse.gitwrapper.GitRepository;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;

import java.io.File;
import java.io.IOException;

public class SimpleGitFixture {
    private static final String simpleGitPath = Resources.getResource("simple-git/dot-git").getPath();

    public static GitRepository getRepository() throws IOException {
        Repository repository = new FileRepositoryBuilder().setGitDir(new File(simpleGitPath))
                .readEnvironment()
                .findGitDir()
                .build();

        return new GitRepository(repository);
    }

    public static Branch getBranch() throws IOException {
        return getRepository().getBranch("master");
    }
}
