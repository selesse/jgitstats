package com.selesse.gitwrapper;

import com.google.common.base.Ascii;

import java.util.List;

public class Commit {
    private String sha;
    private String commitMessage;
    private Author author;
    private Author committer;
    private List<GitFile> filesChanged;

    public Commit(String sha, String commitMessage, Author author, Author committer, List<GitFile> filesChanged) {
        this.sha = sha;
        this.commitMessage = commitMessage;
        this.author = author;
        this.committer = committer;
        this.filesChanged = filesChanged;
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

    public String getSHA() {
        return sha;
    }

    public String getCommitMessage() {
        return commitMessage;
    }

    @Override
    public String toString() {
        return String.format("%s: \"%s\" by %s",
                getSHA().substring(0, 7), Ascii.truncate(getCommitMessage(), 50, "..."), author.getName());
    }
}
