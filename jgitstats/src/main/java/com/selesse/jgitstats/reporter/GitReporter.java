package com.selesse.jgitstats.reporter;

import com.google.common.io.Files;
import com.selesse.jgitstats.git.BranchDetails;
import com.selesse.jgitstats.template.AuthorsTemplate;
import com.selesse.jgitstats.template.IndexTemplate;
import com.selesse.jgitstats.template.LineDiffsTemplate;
import com.selesse.jgitstats.template.RepositoryHeadTemplate;
import com.selesse.jgitstats.template.context.ReportPageContext;
import org.apache.velocity.VelocityContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;

public class GitReporter {
    private static final Logger LOGGER = LoggerFactory.getLogger(GitReporter.class);

    private final String repositoryName;
    private final BranchDetails branchDetails;
    private final String baseDirectory;

    public GitReporter(String repositoryName, BranchDetails branchDetails, String baseDirectory) {
        this.repositoryName = repositoryName;
        this.branchDetails = branchDetails;
        this.baseDirectory = baseDirectory;
    }

    public String getIndexAbsolutePath() {
        File indexFile = new File(baseDirectory, ReportPage.INDEX.getPath());
        return Files.simplifyPath(indexFile.getAbsolutePath());
    }

    public void generateReport() {
        try {
            renderIndex();
            renderRepositoryHead();
            renderLineDiffs();
            renderAuthor();
        } catch (FileNotFoundException e) {
            LOGGER.error("Error creating report", e);
        }
    }

    private void renderIndex() throws FileNotFoundException {
        PrintStream out = getPrintStream(ReportPage.INDEX);
        VelocityContext indexContext = new VelocityContext();

        indexContext.put(ReportPageContext.REPOSITORY_NAME.asAttribute(), repositoryName);
        indexContext.put(ReportPageContext.NUMBER_OF_COMMITS.asAttribute(), branchDetails.getCommits().size());
        indexContext.put(ReportPageContext.BRANCH_NAME.asAttribute(), branchDetails.getBranch().getName());

        IndexTemplate indexTemplate = new IndexTemplate(indexContext);
        indexTemplate.render(out);
    }

    private void renderRepositoryHead() throws FileNotFoundException {
        PrintStream out = getPrintStream(ReportPage.REPOSITORY_HEAD);

        VelocityContext repositoryHeadContext = new VelocityContext();

        repositoryHeadContext.put(ReportPageContext.REPOSITORY_NAME.asAttribute(), repositoryName);
        repositoryHeadContext.put(ReportPageContext.BRANCH_NAME.asAttribute(), branchDetails.getBranch().getName());
        repositoryHeadContext.put(ReportPageContext.GIT_FILES.asAttribute(), branchDetails.getGitFileList());
        repositoryHeadContext.put(ReportPageContext.TOTAL_LINES.asAttribute(), branchDetails.getTotalNumberOfLines());

        RepositoryHeadTemplate repositoryHeadTemplate = new RepositoryHeadTemplate(repositoryHeadContext);
        repositoryHeadTemplate.render(out);
    }

    private void renderLineDiffs() throws FileNotFoundException {
        PrintStream out = getPrintStream(ReportPage.LINE_DIFFS);

        VelocityContext lineDiffContext = new VelocityContext();

        lineDiffContext.put(ReportPageContext.REPOSITORY_NAME.asAttribute(), repositoryName);
        lineDiffContext.put(ReportPageContext.BRANCH_NAME.asAttribute(), branchDetails.getBranch().getName());
        lineDiffContext.put(ReportPageContext.ADDED_LINES.asAttribute(), branchDetails.getTotalLinesAdded());
        lineDiffContext.put(ReportPageContext.REMOVED_LINES.asAttribute(), branchDetails.getTotalLinesRemoved());

        LineDiffsTemplate lineDiffsTemplate = new LineDiffsTemplate(lineDiffContext);
        lineDiffsTemplate.render(out);
    }

    private void renderAuthor() throws FileNotFoundException {
        PrintStream out = getPrintStream(ReportPage.AUTHORS);

        VelocityContext authorContext = new VelocityContext();

        authorContext.put(ReportPageContext.REPOSITORY_NAME.asAttribute(), repositoryName);
        authorContext.put(ReportPageContext.BRANCH_NAME.asAttribute(), branchDetails.getBranch().getName());
        authorContext.put(ReportPageContext.AUTHOR_TO_COMMIT_MAP.asAttribute(), branchDetails.getAuthorToCommitMap());

        AuthorsTemplate authorsTemplate = new AuthorsTemplate(authorContext);
        authorsTemplate.render(out);
    }

    private PrintStream getPrintStream(ReportPage reportPage) throws FileNotFoundException {
        String reportPagePath = reportPage.getPath();

        File file = new File(baseDirectory, reportPagePath);
        File parentFile = file.getParentFile();

        if (!parentFile.exists()) {
            boolean madeDirectories = parentFile.mkdirs();
            if (!madeDirectories) {
                LOGGER.error("Error creating directories {} for {}", parentFile.getAbsolutePath(), reportPagePath);
            }
        }
        return new PrintStream(new FileOutputStream(file));
    }
}
