package com.selesse.jgitstats.git;

public enum IndexContext {
    BRANCH_NAME("branchName"),
    NUMBER_OF_COMMITS("numberOfCommits"),
    GIT_FILES("gitFiles")
    ;

    private final String attributeName;

    IndexContext(String attributeName) {
        this.attributeName = attributeName;
    }

    public String asAttribute() {
        return attributeName;
    }
}
