package com.selesse.jgitstats.cli;

import org.docopt.Docopt;

import java.util.Map;

public class DocOptCli {
    private static final String helpMessage =
            "jgitstats.\n" +
                    "\n" +
                    "Usage:\n" +
                    "  jgitstats <git-repo>...\n" +
                    "  jgitstats -h | --help\n" +
                    "  jgitstats --version\n" +
                    "\n" +
                    "Options:\n" +
                    "  -h --help     Show this screen.\n" +
                    "  --version     Show version.\n";
    private String[] args;

    public DocOptCli(String[] args) {
        this.args = args;
    }

    public Map<String, Object> getOptions() {
        return new Docopt(helpMessage).withVersion("0.1.0").parse(args);
    }
}
