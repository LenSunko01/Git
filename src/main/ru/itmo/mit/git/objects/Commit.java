package ru.itmo.mit.git.objects;

import ru.itmo.mit.git.GitConstants;
import ru.itmo.mit.git.GitException;

public class Commit extends GitObject {
    private String message = "";
    private final String date;
    private String parentCommitSha = "";
    private String treeSha = "";
    public Commit(String date) throws GitException {
        super(Type.commit);
        this.date = date;
    }

    public Commit(String date, String treeSha, String parentCommitSha, String message) throws GitException {
        super(Type.commit);
        this.date = date;
        this.treeSha = treeSha;
        this.parentCommitSha = parentCommitSha;
        this.message = message;
        calculateCommitContent();
    }

    public void calculateCommitContent() {
        var result = new StringBuilder();
        result.append(GitConstants.tree).append(" ").append(getTreeSha()).append(" ");
        if (!(getParentCommitSha().equals(GitConstants.EMPTY))) {
            result.append(getParentCommitSha()).append(" ");
        }
        result.append(getDate()).append(" ").append(getMessage());
        setContent(result.toString());
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setParent(String parentCommitSha) {
        this.parentCommitSha = parentCommitSha;
    }

    public void setTreeSha(String treeSha) {
        this.treeSha = treeSha;
    }

    public String getTreeSha() {
        return treeSha;
    }

    public String getParentCommitSha() {
        return parentCommitSha;
    }

    public String getDate() {
        return date;
    }

    public String getMessage() {
        return message;
    }
}
