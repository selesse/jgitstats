package com.selesse.gitwrapper;

import com.google.common.collect.Lists;
import com.selesse.gitwrapper.objects.FileModes;
import org.eclipse.jgit.lib.FileMode;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.ObjectLoader;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.treewalk.TreeWalk;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;

class Commits {
    private static final Logger LOGGER = LoggerFactory.getLogger(Commits.class);

    public static Commit fromRevCommit(Repository repository, RevCommit revCommit) throws IOException {
        List<GitFile> gitFileList = Lists.newArrayList();

        TreeWalk treeWalk = new TreeWalk(repository);
        treeWalk.addTree(revCommit.getTree());
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

        String commitSHA = revCommit.toObjectId().getName();
        Author author = new Author(revCommit.getAuthorIdent());
        Author committer = new Author(revCommit.getCommitterIdent());

        Instant commitInstant = Instant.ofEpochSecond(revCommit.getCommitTime());
        ZonedDateTime zonedDateTime = ZonedDateTime.ofInstant(commitInstant, ZoneId.systemDefault());

        return new Commit(commitSHA, revCommit.getFullMessage(), zonedDateTime, author, committer, gitFileList);
    }
}
