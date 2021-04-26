package ru.itmo.mit.git.objects;

import org.apache.commons.codec.digest.DigestUtils;

public abstract class GitObject {
    protected String sha;
    public final String type;
    protected String content = "";

    public GitObject(String type, String content) {
        this.type = type;
        this.content = content;
        updateSha();
    }

    public GitObject(String type) {
        this.type = type;
        updateSha();
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
        updateSha();
    }

    protected void updateSha() {
        sha = DigestUtils.sha1Hex(type + " " + content.length() + '\0' + content);
    }

    public String getSha() {
        return sha;
    }
}