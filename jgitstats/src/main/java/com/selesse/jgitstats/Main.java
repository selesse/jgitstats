package com.selesse.jgitstats;

import com.google.common.collect.Lists;
import com.selesse.jgitstats.cli.CommandLine;
import com.selesse.jgitstats.graph.DiffChart;
import com.selesse.jgitstats.template.IndexTemplate;
import com.selesse.jgitwrapper.Branch;
import com.selesse.jgitwrapper.CommitDiff;
import com.selesse.jgitwrapper.CommitDiffs;
import org.apache.velocity.VelocityContext;
import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.List;
import java.util.Map;

public class Main {
    private static final Logger LOGGER = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) throws Exception {
        Map<CommandLine.Option, Object> options = CommandLine.getOptions(args);

        LOGGER.debug("Got {} from arguments", options);

        String gitPath = (String) options.get(CommandLine.Option.GIT_REPO);

        boolean isValidGitPath = isValidGitRoot(gitPath);

        if (!isValidGitPath) {
            LOGGER.error("Error: {} is an invalid Git root", gitPath);
            System.exit(1);
        }

        // This is the default branch to look at
        String branchName = "master";

        if (options.containsKey(CommandLine.Option.BRANCH_NAME)) {
            branchName = (String) options.get(CommandLine.Option.BRANCH_NAME);
        }

        runAnalysisOnLastCommit(new File(gitPath), branchName);
    }

    private static boolean isValidGitRoot(String gitPath) {
        File gitRoot = new File(gitPath);
        if (!gitRoot.exists() || !gitRoot.isDirectory()) {
            return false;
        }

        File gitDirectoryInRoot = new File(gitRoot, ".git");
        return gitDirectoryInRoot.exists() && gitDirectoryInRoot.isDirectory();
    }

    private static void runAnalysisOnLastCommit(File gitDir, String branchName) throws Exception {
        FileRepositoryBuilder builder = new FileRepositoryBuilder();
        LOGGER.info("Looking for a Git repository in {}", gitDir.getAbsolutePath());
        Repository repository = builder.setGitDir(gitDir)
                .readEnvironment() // scan environment GIT_* variables
                .findGitDir() // scan up the file system tree
                .build();

        Branch branch = new Branch(repository, branchName);
        List<RevCommit> commits = branch.getCommits();
        LOGGER.info("Found {} commits on {}", commits.size(), branch.getName());
        LOGGER.info("Commits: {}", commits);

        List<CommitDiff> commitDiffList = Lists.newArrayList();

        if (commitDiffList.size() > 0) {
            for (int i = 0; i < 1; i++) {
                RevCommit commit = commits.get(i);
                List<DiffEntry> diffEntries = CommitDiffs.getDiff(repository, commit);

                for (DiffEntry diffEntry : diffEntries) {
                    CommitDiff commitDiff = new CommitDiff(repository, diffEntry);

                    commitDiffList.add(commitDiff);
                }
                createChartAndIndex(commitDiffList);
            }
        }

        repository.close();
    }

    public static void createChartAndIndex(List<CommitDiff> commitDiffList) throws IOException {

        File indexFile = new File("index.html");
        File diffFile = new File("diff.png");

        DiffChart diffChart = new DiffChart(commitDiffList);
        diffChart.writeChart(new FileOutputStream(diffFile));

        VelocityContext velocityContext = new VelocityContext();
        velocityContext.put("diffChart", diffFile.getAbsolutePath());
        IndexTemplate indexTemplate = new IndexTemplate(velocityContext);

        PrintStream printStream = new PrintStream(new FileOutputStream(indexFile));
        indexTemplate.render(printStream);

        Desktop desktop = Desktop.getDesktop();
        desktop.browse(indexFile.toURI());
    }
}
