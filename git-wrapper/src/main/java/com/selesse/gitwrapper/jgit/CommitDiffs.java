package com.selesse.gitwrapper.jgit;

import com.google.common.collect.Lists;
import com.selesse.gitwrapper.CommitDiff;
import com.selesse.gitwrapper.GitRepository;
import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.diff.DiffFormatter;
import org.eclipse.jgit.diff.RawTextComparator;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.treewalk.CanonicalTreeParser;
import org.eclipse.jgit.treewalk.EmptyTreeIterator;
import org.eclipse.jgit.util.io.DisabledOutputStream;

import java.io.IOException;
import java.util.List;

public class CommitDiffs {
    public static List<CommitDiff> getCommitDiffs(GitRepository gitRepository, RevCommit commit) throws IOException {
        List<CommitDiff> commitDiffList = Lists.newArrayList();

        List<DiffEntry> diffEntries = getDiffs(gitRepository, commit);
        for (DiffEntry diffEntry : diffEntries) {
            CommitDiff commitDiff = new CommitDiff(gitRepository, diffEntry);
            commitDiffList.add(commitDiff);
        }

        return commitDiffList;
    }

    private static List<DiffEntry> getDiffs(GitRepository gitRepository, RevCommit commit) throws IOException {
        List<DiffEntry> diffEntries = Lists.newArrayList();

        Repository repository = gitRepository.getRepository();
        RevWalk revWalk = new RevWalk(repository);

        RevCommit parent = null;
        if (commit.getParentCount() > 0 && commit.getParent(0) != null) {
            parent = revWalk.parseCommit(commit.getParent(0).getId());
        }

        DiffFormatter df = new DiffFormatter(DisabledOutputStream.INSTANCE);
        df.setRepository(repository);
        df.setDiffComparator(RawTextComparator.DEFAULT);
        df.setDetectRenames(true);

        List<DiffEntry> diffs;
        if (parent == null) {
            diffs = df.scan(new EmptyTreeIterator(),
                    new CanonicalTreeParser(null, revWalk.getObjectReader(), commit.getTree()));
        }
        else {
            diffs = df.scan(parent.getTree(), commit.getTree());
        }
        for (DiffEntry diff : diffs) {
            diffEntries.add(diff);
        }

        revWalk.release();

        return diffEntries;
    }
}
