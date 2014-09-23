package com.selesse.jgitstats;

import com.google.common.collect.Lists;
import com.selesse.jgitstats.git.CommitDiff;
import com.selesse.jgitstats.git.CommitDiffs;
import com.selesse.jgitstats.git.Commits;
import com.selesse.jgitstats.graph.DiffChart;
import com.selesse.jgitstats.template.IndexTemplate;
import org.apache.velocity.VelocityContext;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.io.*;
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

        List<CommitDiff> commitDiffList = Lists.newArrayList();

        for (int i = 0; i < 1; i++) {
            RevCommit commit = commits.get(i);
            List<DiffEntry> diffEntries = CommitDiffs.getDiff(repository, commit);

            for (DiffEntry diffEntry : diffEntries) {
                CommitDiff commitDiff = new CommitDiff(repository, diffEntry);

                commitDiffList.add(commitDiff);
            }

        }

        repository.close();

        createChartAndIndex(commitDiffList);
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
