package ru.itmo.mit.git.context;

import ru.itmo.mit.git.GitException;
import ru.itmo.mit.git.objects.Commit;

import java.util.LinkedList;
import java.util.List;

public class GitCommitHistoryService {
    private final GitObjectManager objectManager;

    public GitCommitHistoryService(GitObjectManager objectManager) {
        this.objectManager = objectManager;
    }

    public Commit getParentCommit(String sha, int count) throws GitException {
        var currentCommitSha = sha;
        var currentCommit = objectManager.getCommitBySha(currentCommitSha);
        while (count > 0) {
            currentCommitSha = currentCommit.getParentCommitSha();
            if (currentCommitSha.isEmpty()) {
                throw new GitException("Unknown revision");
            }
            currentCommit = objectManager.getCommitBySha(currentCommitSha);
            count--;
        }
        return currentCommit;
    }

    public List<Commit> getCommitHistoryInclusive(String sha) throws GitException {
        var list = new LinkedList<Commit>();
        if (sha.isEmpty()) {
            return list;
        }
        var currentCommitSha = sha;
        var currentCommit = objectManager.getCommitBySha(currentCommitSha);
        list.add(currentCommit);
        while (true) {
            currentCommitSha = currentCommit.getParentCommitSha();
            if (currentCommitSha.isEmpty()) {
                return list;
            }
            currentCommit = objectManager.getCommitBySha(currentCommitSha);
            list.add(currentCommit);
        }
    }
}
