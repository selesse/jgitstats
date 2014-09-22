package com.selesse.jgitstats;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.errors.IncorrectObjectTypeException;
import org.eclipse.jgit.lib.ObjectReader;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevTree;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;
import org.eclipse.jgit.treewalk.AbstractTreeIterator;
import org.eclipse.jgit.treewalk.CanonicalTreeParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

public class Main {
    private static final Logger LOGGER = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) throws IOException, GitAPIException {
        FileRepositoryBuilder builder = new FileRepositoryBuilder();
        File gitDir = new File(".git/");
        LOGGER.info("Looking for a Git repository in {}", gitDir.getAbsolutePath());
        Repository repository = builder.setGitDir(gitDir)
                .readEnvironment() // scan environment GIT_* variables
                .findGitDir() // scan up the file system tree
                .build();

        Map<String, Ref> refs = repository.getAllRefs();
        for (String refString : refs.keySet()) {
            LOGGER.info("String {}", refString);
            LOGGER.info("Ref {}", refs.get(refString));
        }

        // the diff works on TreeIterators, we prepare two for the two branches
        AbstractTreeIterator oldTreeParser = prepareTreeParser(repository, "refs/heads/master=75cc15633fcc86a0c265b42c79f4a583e534ff8a");
        AbstractTreeIterator newTreeParser = prepareTreeParser(repository, "refs/heads/master=13f4ec148eadaee8359375de46f2e87c3cf59364");

        // then the procelain diff-command returns a list of diff entries
        List<DiffEntry> diff = new Git(repository).diff().setOldTree(oldTreeParser).setNewTree(newTreeParser).call();
        for (DiffEntry entry : diff) {
            LOGGER.info("Entry: " + entry);
        }

        repository.close();
    }

    private static AbstractTreeIterator prepareTreeParser(Repository repository, String ref) throws IOException,
            IncorrectObjectTypeException {
        // from the commit we can build the tree which allows us to construct the TreeParser
        Ref head = repository.getRef(ref);
        RevWalk walk = new RevWalk(repository);
        RevCommit commit = walk.parseCommit(head.getObjectId());
        RevTree tree = walk.parseTree(commit.getTree().getId());

        CanonicalTreeParser oldTreeParser = new CanonicalTreeParser();
        ObjectReader oldReader = repository.newObjectReader();
        try {
            oldTreeParser.reset(oldReader, tree.getId());
        } finally {
            oldReader.release();
        }

        walk.dispose();

        return oldTreeParser;
    }

    public boolean someLibraryMethod() {
        return true;
    }
}
