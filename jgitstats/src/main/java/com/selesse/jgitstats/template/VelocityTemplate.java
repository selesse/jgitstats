package com.selesse.jgitstats.template;

import com.google.common.collect.Lists;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.runtime.RuntimeConstants;
import org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.PrintStream;
import java.io.StringWriter;
import java.util.Properties;

public class VelocityTemplate {
    private static final Logger LOGGER = LoggerFactory.getLogger(VelocityTemplate.class);

    private final String templatePath;
    protected VelocityContext velocityContext;

    public VelocityTemplate(VelocityContext velocityContext, String templatePath) {
        this.velocityContext = velocityContext;
        if (!templatePath.startsWith("velocity/")) {
            templatePath = "velocity/" + templatePath;
        }
        this.templatePath = templatePath;
    }

    public void render(PrintStream out) {
        render(out, templatePath);
    }

    private void render(PrintStream out, String templatePath) {
        Properties velocityProperties = new Properties();
        velocityProperties.setProperty(RuntimeConstants.RESOURCE_LOADER, "classpath");
        velocityProperties.setProperty("classpath.resource.loader.class", ClasspathResourceLoader.class.getName());
        VelocityEngine velocityEngine = new VelocityEngine(velocityProperties);
        velocityEngine.init();

        Template template = velocityEngine.getTemplate(templatePath);

        VelocityContext context = getVelocityContext();
        LOGGER.info("Rendering with context keys: {}", Lists.newArrayList(context.getKeys()));

        StringWriter stringWriter = new StringWriter();
        template.merge(context, stringWriter);

        out.println(stringWriter.toString());
    }

    public VelocityContext getVelocityContext() {
        return velocityContext;
    }

}
