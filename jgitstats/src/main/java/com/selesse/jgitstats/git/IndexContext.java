package com.selesse.jgitstats.git;

public enum IndexContext {
    BRANCH_NAME("branchName"),
    NUMBER_OF_COMMITS("numberOfCommits"),
    ;

    private final String s;

    IndexContext(String s) {
        this.s = s;
    }

    public String asAttribute() {
        return s;
    }
}
