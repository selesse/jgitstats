package com.selesse.jgitstats.browser;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.io.IOException;
import java.net.URI;

public class Browser {
    private static final Logger LOGGER = LoggerFactory.getLogger(Browser.class);

    public static void openPage(String indexAbsolutePath) {
        Desktop desktop = Desktop.getDesktop();
        URI uri = URI.create("file://" + indexAbsolutePath);
        LOGGER.info("Opening {}", uri);

        try {
            desktop.browse(uri);
        }
        catch (IOException e) {
            LOGGER.info("Couldn't browse to {}", uri, e);
        }
    }
}
