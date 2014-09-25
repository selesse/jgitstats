package com.selesse.jgitstats.git;

import com.selesse.jgitstats.template.IndexTemplate;
import org.apache.velocity.VelocityContext;

public class GitReporter {
    private BranchDetails branchDetails;

    public GitReporter(BranchDetails branchDetails) {
        this.branchDetails = branchDetails;
    }

    public void generateReport() {
        VelocityContext velocityContext = new VelocityContext();

        velocityContext.put(IndexContext.NUMBER_OF_COMMITS.asAttribute(), branchDetails.getCommits().size());
        velocityContext.put(IndexContext.BRANCH_NAME.asAttribute(), branchDetails.getBranch().getName());

        IndexTemplate indexTemplate = new IndexTemplate(velocityContext);
        indexTemplate.render(System.out);
    }
}
