package com.selesse.gitwrapper;

import com.google.common.collect.Lists;
import org.eclipse.jgit.lib.FileMode;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.ObjectLoader;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.treewalk.TreeWalk;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class RepositoryReader {
    private static final Logger LOGGER = LoggerFactory.getLogger(RepositoryReader.class);

    public static boolean isValidGitRoot(String path) {
        File gitRoot = new File(path);
        if (!gitRoot.exists() || !gitRoot.isDirectory()) {
            return false;
        }

        File gitDirectoryInRoot = new File(gitRoot, ".git");
        return gitDirectoryInRoot.exists() && gitDirectoryInRoot.isDirectory();
    }

    public static List<GitFile> loadRepositoryLastCommit(Repository repository, Branch branch) throws IOException {
        List<GitFile> gitFileList = Lists.newArrayList();

        ObjectId lastCommitId = repository.resolve(branch.getName());
        RevWalk revWalk = new RevWalk(repository);
        RevCommit commit = revWalk.parseCommit(lastCommitId);

        TreeWalk treeWalk = new TreeWalk(repository);
        treeWalk.addTree(commit.getTree());
        while (treeWalk.next()) {
            int objectIdIndex = 0;

            String pathString = treeWalk.getPathString();
            FileMode fileMode = treeWalk.getFileMode(objectIdIndex);

            LOGGER.debug("{} : {}", FileModes.asString(fileMode), pathString);

            if (treeWalk.isSubtree()) {
                treeWalk.enterSubtree();
            }
            else {
                ObjectLoader objectLoader = repository.open(treeWalk.getObjectId(objectIdIndex));
                byte[] fileBytes = objectLoader.getBytes();

                GitFile gitFile = new GitFile(pathString, treeWalk.getFileMode(objectIdIndex), fileBytes);
                gitFileList.add(gitFile);
            }
        }

        treeWalk.release();

        return gitFileList;
    }
}
