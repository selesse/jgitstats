package com.selesse.jgitstats.git;

import com.selesse.jgitstats.template.IndexTemplate;
import org.apache.velocity.VelocityContext;

import java.io.PrintStream;

public class GitReporter {
    private final BranchDetails branchDetails;
    private PrintStream out;

    public GitReporter(BranchDetails branchDetails) {
        this.branchDetails = branchDetails;
        out = System.out;
    }

    public GitReporter(BranchDetails branchDetails, PrintStream out) {
        this(branchDetails);
        this.out = out;
    }

    public void generateReport() {
        VelocityContext velocityContext = new VelocityContext();

        velocityContext.put(IndexContext.NUMBER_OF_COMMITS.asAttribute(), branchDetails.getCommits().size());
        velocityContext.put(IndexContext.BRANCH_NAME.asAttribute(), branchDetails.getBranch().getName());
        velocityContext.put(IndexContext.GIT_FILES.asAttribute(), branchDetails.getGitFileList());

        IndexTemplate indexTemplate = new IndexTemplate(velocityContext);
        indexTemplate.render(out);
    }
}
