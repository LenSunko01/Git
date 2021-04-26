package ru.itmo.mit.git.objects;

public class Commit extends GitObject {
    private String message;
    private final String date;
    private String parentCommitSha;
    private String treeSha;
    public Commit(String date) {
        super("commit");
        this.date = date;
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
