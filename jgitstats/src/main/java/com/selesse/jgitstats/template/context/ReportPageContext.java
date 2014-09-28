package com.selesse.jgitstats.template.context;

public enum ReportPageContext {
    BRANCH_NAME("branchName"),
    NUMBER_OF_COMMITS("numberOfCommits"),
    GIT_FILES("gitFiles")
    ;

    private final String attributeName;

    ReportPageContext(String attributeName) {
        this.attributeName = attributeName;
    }

    public String asAttribute() {
        return attributeName;
    }
}
