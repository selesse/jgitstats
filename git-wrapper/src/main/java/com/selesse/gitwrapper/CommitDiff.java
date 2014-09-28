package com.selesse.gitwrapper;

import com.google.common.base.Splitter;
import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.diff.DiffFormatter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

public class CommitDiff {
    private static final Logger LOGGER = LoggerFactory.getLogger(CommitDiff.class);
    private final GitRepository repository;

    private String oldPath;
    private String newPath;
    private int linesAdded;
    private int linesRemoved;

    public CommitDiff(GitRepository repository, DiffEntry diffEntry) throws IOException {
        this.repository = repository;
        this.oldPath = diffEntry.getOldPath();
        this.newPath = diffEntry.getNewPath();
        this.linesAdded = 0;
        this.linesRemoved = 0;

        calculateLines(diffEntry);
    }

    private void calculateLines(DiffEntry diffEntry) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        DiffFormatter diffFormatter = new DiffFormatter(out);
        diffFormatter.setRepository(repository.getRepository());
        diffFormatter.setContext(0);

        LOGGER.info("Diff stats for {} -> {}", diffEntry.getOldPath(), diffEntry.getNewPath());
        diffFormatter.format(diffEntry);

        String diffText = out.toString("UTF-8");
        List<String> changes = Splitter.onPattern("\r?\n").splitToList(diffText);

        int diffStartPosition = 0;
        for (String change : changes) {
            if (change.startsWith("@@")) {
                break;
            }
            diffStartPosition++;
        }

        for (String change : changes.subList(diffStartPosition, changes.size())) {
            if (change.startsWith("+")) {
                linesAdded++;
            } else if (change.startsWith("-")) {
                linesRemoved++;
            }
        }

        LOGGER.info("{} -> {}: {} additions, {} removals", oldPath, newPath, linesAdded, linesRemoved);

        out.reset();
        diffFormatter.release();
    }

    public String getOldPath() {
        return oldPath;
    }

    public void setOldPath(String oldPath) {
        this.oldPath = oldPath;
    }

    public String getNewPath() {
        return newPath;
    }

    public void setNewPath(String newPath) {
        this.newPath = newPath;
    }

    public int getLinesAdded() {
        return linesAdded;
    }

    public void setLinesAdded(int linesAdded) {
        this.linesAdded = linesAdded;
    }

    public int getLinesRemoved() {
        return linesRemoved;
    }

    public void setLinesRemoved(int linesRemoved) {
        this.linesRemoved = linesRemoved;
    }

}
