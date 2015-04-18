package com.selesse.gitwrapper;

import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;

import java.io.File;
import java.io.IOException;

public class GitRepositoryReader {
    public static boolean isValidGitRoot(String path) {
        File gitRoot = new File(path);
        if (!gitRoot.isDirectory()) {
            return false;
        }

        File gitDirectoryInRoot = new File(gitRoot, ".git");
        return gitDirectoryInRoot.isDirectory();
    }

    public static GitRepository loadRepository(File directory) throws IOException {
        if (!isValidGitRoot(directory.getAbsolutePath())) {
            throw new IOException("Invalid Git root: " + directory.getAbsolutePath());
        }

        Repository repository = new FileRepositoryBuilder().findGitDir(directory).build();
        return new GitRepository(repository);
    }

    public static Commit loadLastCommit(GitRepository gitRepository, Branch branch) throws IOException {
        Repository repository = gitRepository.getRepository();

        ObjectId lastCommitId = repository.resolve(branch.getName());
        RevWalk revWalk = new RevWalk(repository);
        Commit commit = Commits.fromRevCommit(repository, revWalk.parseCommit(lastCommitId));
        revWalk.release();

        return commit;
    }
}
