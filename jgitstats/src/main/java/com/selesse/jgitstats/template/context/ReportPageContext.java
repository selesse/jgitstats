package com.selesse.jgitstats.template.context;

public enum ReportPageContext {
    BRANCH_NAME("branchName"),
    NUMBER_OF_COMMITS("numberOfCommits"),
    TOTAL_LINES("totalLines"),
    GIT_FILES("gitFiles"),
    ADDED_LINES("addedLines"),
    REMOVED_LINES("removedLines"),
    ;

    private final String attributeName;

    ReportPageContext(String attributeName) {
        this.attributeName = attributeName;
    }

    public String asAttribute() {
        return attributeName;
    }
}
