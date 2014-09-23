package com.selesse.jgitstats;

import com.google.common.base.Splitter;
import com.selesse.jgitstats.git.CommitDiffs;
import com.selesse.jgitstats.git.Commits;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.diff.DiffFormatter;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.List;

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

        String branch = "master";

        List<RevCommit> commits = Commits.getCommits(repository, branch );
        LOGGER.info("Found {} commits on {}", commits.size(), branch);
        LOGGER.info("Commits: {}", commits);

        RevCommit thirdCommit = commits.get(0);
        RevCommit secondCommit = commits.get(1);
        RevCommit firstCommit = commits.get(2);

        RevCommit chosenCommit = thirdCommit;

        printCommitDiff(repository, chosenCommit);

        repository.close();
    }

    private static void printCommitDiff(Repository repository, RevCommit chosenCommit) throws IOException {
        List<DiffEntry> diffs = CommitDiffs.getDiff(repository, chosenCommit);
        LOGGER.info("Found {} diffs for commit {}", diffs.size(), chosenCommit.getName());

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        DiffFormatter diffFormatter = new DiffFormatter(out);
        diffFormatter.setRepository(repository);
        diffFormatter.setContext(0);

        int totalAdditions = 0;
        int totalRemovals = 0;

        for (DiffEntry diffEntry : diffs) {
            LOGGER.info("Diff stats for {} -> {}", diffEntry.getOldPath(), diffEntry.getNewPath());
            diffFormatter.format(diffEntry);

            String diffText = out.toString("UTF-8");
            List<String> changes = Splitter.onPattern("\r?\n").splitToList(diffText);
            int numberOfAdditions = 0;
            int numberOfRemovals = 0;

            int diffStartPosition = 0;
            for (String change : changes) {
                if (change.startsWith("@@")) {
                    break;
                }
                diffStartPosition++;
            }

            for (String change : changes.subList(diffStartPosition, changes.size())) {
                if (change.startsWith("+")) {
                    numberOfAdditions++;
                }
                else if (change.startsWith("-")) {
                    numberOfRemovals++;
                }
            }

            totalAdditions += numberOfAdditions;
            totalRemovals += numberOfRemovals;
            LOGGER.info("{} additions, {} removals, {} total", numberOfAdditions, numberOfRemovals, numberOfAdditions - numberOfRemovals);

            out.reset();
        }
        LOGGER.info("Grand total: {} additions, {} removals, {} total", totalAdditions, totalRemovals, totalAdditions - totalRemovals);

        diffFormatter.release();
    }
}
