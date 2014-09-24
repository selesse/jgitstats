package com.selesse.gitwrapper;

import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.ObjectLoader;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevTree;
import org.eclipse.jgit.treewalk.TreeWalk;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.PrintStream;
import java.util.List;

public class Commits {
    private static final Logger LOGGER = LoggerFactory.getLogger(Commits.class);

    private Commits() {}

    public static void printCommitInformation(Repository repository, List<RevCommit> revCommitList,
                                              PrintStream printStream) throws IOException {
        for (RevCommit commit : revCommitList) {
            LOGGER.info("Displaying info for commit {}", commit.getName());
            RevTree revTree = commit.getTree();
            TreeWalk treeWalk = new TreeWalk(repository);
            treeWalk.addTree(revTree);
            treeWalk.setRecursive(true);

            do {
                ObjectId objectId = treeWalk.getObjectId(0);
                ObjectLoader loader = repository.open(objectId);

                // and then one can the loader to read the file
                loader.copyTo(printStream);
            } while (treeWalk.next());

            treeWalk.release();
            LOGGER.info("Done displaying info for commit {}", commit.getName());
        }
    }
}
