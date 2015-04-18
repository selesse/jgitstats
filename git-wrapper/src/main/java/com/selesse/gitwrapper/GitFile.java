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
    private byte[] byteContents;
    private List<String> contents;

    public GitFile(String path, FileMode fileMode, byte[] bytes) {
        this.path = path;
        this.fileMode = fileMode;
        this.isBinary = RawText.isBinary(bytes);
        this.byteContents = bytes;
    }

    public int getNumberOfLines() {
        return getContents().size();
    }

    public List<String> getContents() {
        if (contents == null) {
            try {
                // UTF-8 is the only encoding, ever, right?
                String fileContents = new String(byteContents, "UTF-8");
                contents = Splitter.onPattern("\r?\n").splitToList(fileContents);
            }
            catch (UnsupportedEncodingException e) {
                throw new IllegalArgumentException("Error reading file: " + path);
            }
        }
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
