package com.selesse.gitwrapper;

import com.google.common.base.Splitter;
import org.eclipse.jgit.diff.RawText;
import org.eclipse.jgit.lib.FileMode;

import java.io.UnsupportedEncodingException;
import java.util.List;

public class GitFile {
    private String path;
    private FileMode fileMode;
    private boolean isBinary;
    private List<String> contents;
    private int numberOfLines;

    public GitFile(String path, FileMode fileMode, byte[] bytes) throws UnsupportedEncodingException {
        this.path = path;
        this.fileMode = fileMode;
        this.isBinary = RawText.isBinary(bytes);

        // UTF-8 is literally the only encoding ever, right?
        String fileContents = new String(bytes, "UTF-8");
        this.contents = Splitter.onPattern("\r?\n").splitToList(fileContents);
        this.numberOfLines = contents.size();
    }

    public int getNumberOfLines() {
        return numberOfLines;
    }

    public List<String> getContents() {
        return contents;
    }

    public boolean isBinary() {
        return isBinary;
    }

    public FileMode getFileMode() {
        return fileMode;
    }

    public String getPath() {
        return path;
    }

}
