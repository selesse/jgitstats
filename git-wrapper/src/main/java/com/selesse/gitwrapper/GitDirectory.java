package com.selesse.gitwrapper;

import java.io.File;

public class GitDirectory {
    public static boolean isValidGitRoot(String path) {
        File gitRoot = new File(path);
        if (!gitRoot.exists() || !gitRoot.isDirectory()) {
            return false;
        }

        File gitDirectoryInRoot = new File(gitRoot, ".git");
        return gitDirectoryInRoot.exists() && gitDirectoryInRoot.isDirectory();
    }
}
