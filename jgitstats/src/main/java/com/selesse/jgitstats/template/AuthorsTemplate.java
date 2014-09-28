package com.selesse.jgitstats.template;

import org.apache.velocity.VelocityContext;

public class AuthorsTemplate extends VelocityTemplate {
    public AuthorsTemplate(VelocityContext velocityContext) {
        super(velocityContext, "authors.vm");
    }
}
