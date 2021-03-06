package com.selesse.gitwrapper;

import com.google.common.collect.Lists;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.Constants;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevWalk;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public class Branch {
    private Repository repository;
    private String name;
    private List<Commit> revCommitList;

    Branch(Repository repository, String name) {
        this.repository = repository;
        this.name = name;
    }

    public List<Commit> getCommits() throws GitAPIException, IOException {
        if (revCommitList == null) {
            revCommitList = Lists.newArrayList();

            Git git = new Git(repository);
            RevWalk walk = new RevWalk(repository);

            List<Ref> branches = git.branchList().call();

            for (Ref branch : branches) {
                String currentBranchName = branch.getName();
                String desiredBranch = Constants.R_HEADS + name;
                if (!desiredBranch.equals(currentBranchName)) {
                    continue;
                }

                Iterable<RevCommit> commits = git.log().all().call();

                for (RevCommit commit : commits) {
                    boolean foundInThisBranch = false;

                    RevCommit targetCommit = walk.parseCommit(repository.resolve(commit.getName()));
                    for (Map.Entry<String, Ref> stringRefEntry : repository.getAllRefs().entrySet()) {
                        if (stringRefEntry.getKey().startsWith(Constants.R_HEADS)) {
                            Ref ref = stringRefEntry.getValue();
                            if (walk.isMergedInto(targetCommit, walk.parseCommit(ref.getObjectId()))) {
                                String foundInBranch = ref.getName();
                                if (currentBranchName.equals(foundInBranch)) {
                                    foundInThisBranch = true;
                                    break;
                                }
                            }
                        }
                    }

                    if (foundInThisBranch) {
                        revCommitList.add(Commits.fromRevCommit(repository, commit));
                    }
                }
            }
        }
        return revCommitList;
    }

    public String getName() {
        return name;
    }
}
