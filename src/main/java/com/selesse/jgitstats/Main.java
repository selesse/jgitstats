package com.selesse.jgitstats;

import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.Map;

public class Main {
    private static final Logger LOGGER = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) throws IOException {
        FileRepositoryBuilder builder = new FileRepositoryBuilder();
        File gitDir = new File(".git/");
        LOGGER.info("Looking for a Git repository in {}", gitDir.getAbsolutePath());
        Repository repository = builder.setGitDir(gitDir)
                .readEnvironment() // scan environment GIT_* variables
                .findGitDir() // scan up the file system tree
                .build();

        LOGGER.info("Found {} with {} refs", repository, repository.getAllRefs().size());
        Map<String, Ref> refs = repository.getAllRefs();
        for (Map.Entry<String, Ref> stringRefEntry : refs.entrySet()) {
            String string = stringRefEntry.getKey();
            Ref ref = stringRefEntry.getValue();

            LOGGER.info("{} => {}", string, ref);
        }
    }

    public boolean someLibraryMethod() {
        return true;
    }
}
