package ru.itmo.mit.git.objects;

import ru.itmo.mit.git.GitConstants;

public class Commit extends GitObject {
    private String message = "";
    private final String date;
    private String parentCommitSha = "";
    private String treeSha = "";
    public Commit(String date) {
        super("commit");
        this.date = date;
    }

    public Commit(String date, String treeSha, String parentCommitSha, String message) {
        super("commit");
        this.date = date;
        this.treeSha = treeSha;
        this.parentCommitSha = parentCommitSha;
        this.message = message;
        calculateCommitContent();
    }

    /*tree sha parentCommitSha date message*/
    public void calculateCommitContent() {
        var result = new StringBuilder();
        result.append("tree ").append(getTreeSha()).append(" ");
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
