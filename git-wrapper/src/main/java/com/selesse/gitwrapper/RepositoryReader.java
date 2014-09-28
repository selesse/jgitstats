package com.selesse.gitwrapper;

import com.google.common.collect.Lists;
import org.eclipse.jgit.lib.FileMode;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.ObjectLoader;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;
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
        return gitDirectoryInRoot.isDirectory();
    }

    public static GitRepository loadRepository(File directory) throws IOException {
        if (!isValidGitRoot(directory.getAbsolutePath())) {
            throw new IOException("Invalid Git root " + directory.getAbsolutePath());
        }

        Repository repository = new FileRepositoryBuilder().findGitDir(directory).build();
        return new GitRepository(repository);
    }

    public static List<GitFile> loadRepositoryLastCommit(GitRepository gitRepository, Branch branch)
            throws IOException {
        List<GitFile> gitFileList = Lists.newArrayList();

        Repository repository = gitRepository.getRepository();

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
                ObjectId objectId = treeWalk.getObjectId(objectIdIndex);
                ObjectLoader objectLoader = repository.open(objectId);
                byte[] fileBytes = objectLoader.getBytes();

                GitFile gitFile = new GitFile(pathString, treeWalk.getFileMode(objectIdIndex), fileBytes);
                gitFileList.add(gitFile);
            }
        }

        treeWalk.release();

        return gitFileList;
    }
}
