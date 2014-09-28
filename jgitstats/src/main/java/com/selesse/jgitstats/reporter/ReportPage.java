package com.selesse.jgitstats.reporter;

public enum ReportPage {
    INDEX("index.html"),
    REPOSITORY_HEAD("repository-head.html"),
    ;

    private final String relativePath;

    ReportPage(String relativePath) {
        this.relativePath = relativePath;
    }

    public String getPath() {
        return relativePath;
    }
}
