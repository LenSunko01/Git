package ru.itmo.mit.git;

import ru.itmo.mit.git.objects.Commit;
import ru.itmo.mit.git.objects.GitObject;

import java.util.ArrayList;
import java.util.List;

public class GitCommitHistoryService {
    private final GitObjectManager objectManager = GitObjectManager.getInstance();

    private GitCommitHistoryService() {}
    private static final GitCommitHistoryService instance = new GitCommitHistoryService();
    public static GitCommitHistoryService getInstance() {
        return instance;
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
        var list = new ArrayList<Commit>();
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
