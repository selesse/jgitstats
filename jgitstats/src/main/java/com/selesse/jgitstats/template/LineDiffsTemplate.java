package com.selesse.jgitstats.template;

import org.apache.velocity.VelocityContext;

public class LineDiffsTemplate extends VelocityTemplate {
    public LineDiffsTemplate(VelocityContext velocityContext) {
        super(velocityContext, "line-diffs.vm");
    }
}
