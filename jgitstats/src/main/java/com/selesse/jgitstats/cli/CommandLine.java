package com.selesse.jgitstats.cli;

import com.google.common.collect.Maps;
import org.docopt.Docopt;

import java.util.Map;

public class CommandLine {
    private static final String DEFAULT_BRANCH = "master";
    private static final String helpMessage =
            "jgitstats.\n" +
                    "\n" +
                    "Usage:\n" +
                    "  jgitstats " + Option.GIT_REPO + " [" + Option.BRANCH_NAME + "=name]\n" +
                    "  jgitstats -h | --help\n" +
                    "  jgitstats -v | --version\n" +
                    "\n" +
                    "Options:\n" +
                    "  -h --help         Show this screen.\n" +
                    "  -v --version      Show version.\n" +
                    "  --branch=<name>   Choose branch to run on [default: " + DEFAULT_BRANCH + "].\n";

    private CommandLine() {
    }

    public static Map<Option, Object> getOptions(String[] args) {
        Map<String, Object> options = new Docopt(helpMessage).withVersion("0.1.0").parse(args);

        Map<Option, Object> optionObjectMap = Maps.newHashMap();

        for (Map.Entry<String, Object> stringObjectEntry : options.entrySet()) {
            String string = stringObjectEntry.getKey();
            Object value = stringObjectEntry.getValue();

            Option option = Option.fromString(string);
            if (option != null) {
                optionObjectMap.put(option, value);
            }
        }

        return optionObjectMap;
    }

    public enum Option {
        GIT_REPO("<git-repo>"),
        BRANCH_NAME("--branch")
        ;

        private final String docOptString;

        Option(String docOptString) {
            this.docOptString = docOptString;
        }

        @Override
        public String toString() {
            return docOptString;
        }

        public static Option fromString(String string) {
            if (string.equals("<git-repo>")) {
                return GIT_REPO;
            } else if (string.equals("--branch")) {
                return BRANCH_NAME;
            }
            return null;
        }
    }

}
