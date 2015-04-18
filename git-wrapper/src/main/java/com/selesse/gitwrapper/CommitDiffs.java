package com.selesse.gitwrapper;

import com.google.common.collect.Lists;
import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.diff.DiffFormatter;
import org.eclipse.jgit.diff.RawTextComparator;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.treewalk.CanonicalTreeParser;
import org.eclipse.jgit.treewalk.EmptyTreeIterator;
import org.eclipse.jgit.util.io.DisabledOutputStream;

import java.io.IOException;
import java.util.List;

class CommitDiffs {
    static List<CommitDiff> getCommitDiffs(Repository repository, Commit commit) throws IOException {
        List<CommitDiff> commitDiffList = Lists.newArrayList();

        List<DiffEntry> diffEntries = getDiffs(repository, commit);
        for (DiffEntry diffEntry : diffEntries) {
            CommitDiff commitDiff = new CommitDiff(repository, diffEntry);
            commitDiffList.add(commitDiff);
        }

        return commitDiffList;
    }

    private static List<DiffEntry> getDiffs(Repository repository, Commit commit) throws IOException {
        List<DiffEntry> diffEntries = Lists.newArrayList();

        RevWalk revWalk = new RevWalk(repository);

        RevCommit parent = null;
        ObjectId id = ObjectId.fromString(commit.getSHA());
        RevCommit revCommit = revWalk.parseCommit(id);
        if (revCommit.getParentCount() > 0 && revCommit.getParent(0) != null) {
            parent = revWalk.parseCommit(revCommit.getParent(0).getId());
        }

        DiffFormatter diffFormatter = new DiffFormatter(DisabledOutputStream.INSTANCE);
        diffFormatter.setRepository(repository);
        diffFormatter.setDiffComparator(RawTextComparator.DEFAULT);
        diffFormatter.setDetectRenames(true);

        List<DiffEntry> diffs;
        if (parent == null) {
            EmptyTreeIterator emptyTreeIterator = new EmptyTreeIterator();
            CanonicalTreeParser canonicalTreeParser =
                    new CanonicalTreeParser(null, revWalk.getObjectReader(), revCommit.getTree());
            diffs = diffFormatter.scan(emptyTreeIterator, canonicalTreeParser);
        }
        else {
            diffs = diffFormatter.scan(parent.getTree(), revCommit.getTree());
        }

        for (DiffEntry diff : diffs) {
            diffEntries.add(diff);
        }

        revWalk.release();

        return diffEntries;
    }
}
