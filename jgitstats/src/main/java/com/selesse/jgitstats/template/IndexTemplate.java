package com.selesse.jgitstats.template;

import org.apache.velocity.VelocityContext;

public class IndexTemplate extends VelocityTemplate {
    public IndexTemplate(VelocityContext velocityContext) {
        super(velocityContext, "index.vm");
    }
}
