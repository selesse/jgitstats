package com.selesse.jgitstats.reporter;

import com.google.common.io.Files;
import com.selesse.jgitstats.git.BranchDetails;
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

    private final BranchDetails branchDetails;
    private final String baseDirectory;

    public GitReporter(BranchDetails branchDetails, String baseDirectory) {
        this.branchDetails = branchDetails;
        this.baseDirectory = baseDirectory;
    }

    public String getIndexAbsolutePath() {
        File indexFile = new File(baseDirectory, ReportPage.INDEX.getPath());
        return Files.simplifyPath(indexFile.getAbsolutePath());
    }

    public void generateReport() {
        try {
            renderIndex(getPrintStream(ReportPage.INDEX));
            renderRepositoryHead(getPrintStream(ReportPage.REPOSITORY_HEAD));
            renderLineDiffs(getPrintStream(ReportPage.LINE_DIFFS));
        } catch (FileNotFoundException e) {
            LOGGER.error("Error creating report", e);
        }
    }

    private void renderIndex(PrintStream out) {
        VelocityContext indexContext = new VelocityContext();

        indexContext.put(ReportPageContext.NUMBER_OF_COMMITS.asAttribute(), branchDetails.getCommits().size());
        indexContext.put(ReportPageContext.BRANCH_NAME.asAttribute(), branchDetails.getBranch().getName());

        IndexTemplate indexTemplate = new IndexTemplate(indexContext);
        indexTemplate.render(out);
    }

    private void renderRepositoryHead(PrintStream out) {
        VelocityContext repositoryHeadContext = new VelocityContext();

        repositoryHeadContext.put(ReportPageContext.BRANCH_NAME.asAttribute(), branchDetails.getBranch().getName());
        repositoryHeadContext.put(ReportPageContext.GIT_FILES.asAttribute(), branchDetails.getGitFileList());
        repositoryHeadContext.put(ReportPageContext.TOTAL_LINES.asAttribute(), branchDetails.getTotalNumberOfLines());

        RepositoryHeadTemplate repositoryHeadTemplate = new RepositoryHeadTemplate(repositoryHeadContext);
        repositoryHeadTemplate.render(out);
    }

    private void renderLineDiffs(PrintStream out) {
        VelocityContext lineDiffContext = new VelocityContext();

        lineDiffContext.put(ReportPageContext.BRANCH_NAME.asAttribute(), branchDetails.getBranch().getName());
        lineDiffContext.put(ReportPageContext.ADDED_LINES.asAttribute(), branchDetails.getTotalLinesAdded());
        lineDiffContext.put(ReportPageContext.REMOVED_LINES.asAttribute(), branchDetails.getTotalLinesRemoved());

        LineDiffsTemplate lineDiffsTemplate = new LineDiffsTemplate(lineDiffContext);
        lineDiffsTemplate.render(out);
    }


    private PrintStream getPrintStream(ReportPage reportPage) throws FileNotFoundException {
        String reportPagePath = reportPage.getPath();

        File file = new File(baseDirectory, reportPagePath);
        File parentFile = file.getParentFile();

        boolean madeDirectories = parentFile.mkdirs();
        if (!madeDirectories) {
            LOGGER.error("Error creating directories {} for {}", parentFile.getAbsolutePath(), reportPagePath);
        }
        return new PrintStream(new FileOutputStream(file));
    }
}
