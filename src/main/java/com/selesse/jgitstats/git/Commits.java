package com.selesse.jgitstats.git;

import com.google.common.collect.Lists;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.*;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevTree;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.treewalk.TreeWalk;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.PrintStream;
import java.util.List;
import java.util.Map;

public class Commits {
    private static final Logger LOGGER = LoggerFactory.getLogger(Commits.class);

    private Commits() {}

    public static List<RevCommit> getCommits(Repository repository, String branchName) throws GitAPIException, IOException {
        List<RevCommit> revCommitList = Lists.newArrayList();

        Git git = new Git(repository);
        RevWalk walk = new RevWalk(repository);

        List<Ref> branches = git.branchList().call();

        for (Ref branch : branches) {
            String currentBranchName = branch.getName();
            String desiredBranch = Constants.R_HEADS + branchName;
            if (!desiredBranch.equals(currentBranchName)) {
                LOGGER.debug("Current branch {} is not {}", currentBranchName, branchName);
                continue;
            }

            Iterable<RevCommit> commits = git.log().all().call();

            for (RevCommit commit : commits) {
                boolean foundInThisBranch = false;

                RevCommit targetCommit = walk.parseCommit(repository.resolve(commit.getName()));
                for (Map.Entry<String, Ref> stringRefEntry : repository.getAllRefs().entrySet()) {
                    if (stringRefEntry.getKey().startsWith(Constants.R_HEADS)) {
                        Ref ref = stringRefEntry.getValue();
                        if (walk.isMergedInto(targetCommit, walk.parseCommit(ref.getObjectId()))) {
                            String foundInBranch = ref.getName();
                            if (currentBranchName.equals(foundInBranch)) {
                                foundInThisBranch = true;
                                break;
                            }
                        }
                    }
                }

                if (foundInThisBranch) {
                    revCommitList.add(commit);
                }
            }
        }
        return revCommitList;
    }

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
