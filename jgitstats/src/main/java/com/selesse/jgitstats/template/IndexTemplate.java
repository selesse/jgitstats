package com.selesse.jgitstats.template;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.runtime.RuntimeConstants;
import org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader;

import java.io.PrintStream;
import java.io.StringWriter;
import java.util.Properties;

public class IndexTemplate {
    private VelocityContext velocityContext;

    public IndexTemplate(VelocityContext velocityContext) {
        this.velocityContext = velocityContext;
    }

    public void render(PrintStream out) {
        Properties velocityProperties = new Properties();
        velocityProperties.setProperty(RuntimeConstants.RESOURCE_LOADER, "classpath");
        velocityProperties.setProperty("classpath.resource.loader.class", ClasspathResourceLoader.class.getName());
        VelocityEngine velocityEngine = new VelocityEngine(velocityProperties);
        velocityEngine.init();

        Template template = velocityEngine.getTemplate("velocity/index.vm");

        VelocityContext context = getVelocityContext();

        StringWriter stringWriter = new StringWriter();
        template.merge(context, stringWriter);

        out.println(stringWriter.toString());
    }

    public VelocityContext getVelocityContext() {
        return velocityContext;
    }

    public void setVelocityContext(VelocityContext velocityContext) {
        this.velocityContext = velocityContext;
    }
}
