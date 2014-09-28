package com.selesse.jgitstats.template;

import org.apache.velocity.VelocityContext;

public class RepositoryHeadTemplate extends VelocityTemplate {
    public RepositoryHeadTemplate(VelocityContext repositoryHeadContext) {
        super(repositoryHeadContext, "repository-head.vm");
    }
}
