package com.selesse.gitwrapper;

import com.google.common.base.Ascii;

import java.time.ZonedDateTime;
import java.util.List;

public class Commit {
    private String sha;
    private String commitMessage;
    private ZonedDateTime commitDateTime;
    private Author author;
    private Author committer;
    private List<GitFile> filesChanged;

    public Commit(String sha,
                  String commitMessage,
                  ZonedDateTime commitDateTime,
                  Author author,
                  Author committer,
                  List<GitFile> filesChanged) {
        this.sha = sha;
        this.commitMessage = commitMessage;
        this.commitDateTime = commitDateTime;
        this.author = author;
        this.committer = committer;
        this.filesChanged = filesChanged;
    }

    public String getSHA() {
        return sha;
    }

    public String getCommitMessage() {
        return commitMessage;
    }

    public ZonedDateTime getCommitDateTime() {
        return commitDateTime;
    }

    public Author getAuthor() {
        return author;
    }

    public Author getCommitter() {
        return committer;
    }

    public List<GitFile> getFilesChanged() {
        return filesChanged;
    }

    @Override
    public String toString() {
        return String.format("%s: \"%s\" by %s",
                getSHA().substring(0, 7), Ascii.truncate(getCommitMessage(), 50, "..."), author.getName());
    }
}
