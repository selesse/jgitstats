package com.selesse.gitwrapper.objects;

import org.eclipse.jgit.lib.FileMode;

public class FileModes {
    public static String asString(FileMode fileMode) {
        if (fileMode.equals(FileMode.EXECUTABLE_FILE)) {
            return "executable file";
        }
        else if (fileMode.equals(FileMode.REGULAR_FILE)) {
            return "normal file";
        }
        else if (fileMode.equals(FileMode.TREE)) {
            return "directory";
        }
        else if (fileMode.equals(FileMode.SYMLINK)) {
            return "symlink";
        }
        else if (fileMode.equals(FileMode.GITLINK)) {
            return "Git link";
        }
        else {
            return fileMode.toString();
        }
    }
}
