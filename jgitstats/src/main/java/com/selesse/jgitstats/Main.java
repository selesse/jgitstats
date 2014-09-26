package com.selesse.jgitstats;

import com.selesse.gitwrapper.RepositoryReader;
import com.selesse.jgitstats.cli.CommandLine;
import com.selesse.jgitstats.git.BranchAnalyzer;
import com.selesse.jgitstats.git.BranchDetails;
import com.selesse.jgitstats.git.GitReporter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.Map;

public class Main {
    private static final Logger LOGGER = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) throws Exception {
        Map<CommandLine.Option, Object> options = CommandLine.getOptions(args);

        LOGGER.debug("Got {} from arguments", options);

        String gitPath = (String) options.get(CommandLine.Option.GIT_REPO);

        boolean isValidGitPath = RepositoryReader.isValidGitRoot(gitPath);

        if (!isValidGitPath) {
            LOGGER.error("Error: {} is an invalid Git root", gitPath);
            System.exit(1);
        }

        // This is the default branch to look at
        String branchName = "master";

        if (options.containsKey(CommandLine.Option.BRANCH_NAME)) {
            branchName = (String) options.get(CommandLine.Option.BRANCH_NAME);
        }

        File gitRoot = new File(gitPath, ".git");
        LOGGER.debug("Creating a BranchAnalyzer for {} on branch {}", gitRoot.getAbsolutePath(), branchName);
        BranchAnalyzer branchAnalyzer = new BranchAnalyzer(gitRoot, branchName);
        BranchDetails branchDetails = branchAnalyzer.getBranchDetails();

        GitReporter gitReporter = new GitReporter(branchDetails, new PrintStream(new FileOutputStream("index.html")));
        gitReporter.generateReport();
    }
}
