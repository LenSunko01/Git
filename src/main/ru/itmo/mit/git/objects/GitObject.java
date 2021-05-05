package ru.itmo.mit.git.objects;

import org.apache.commons.codec.digest.DigestUtils;
import ru.itmo.mit.git.GitException;

public abstract class GitObject {
    protected enum Type {
        blob, tree, commit
    }
    protected String sha;
    public final Type type;
    protected String content = "";

    public GitObject(Type type, String content) throws GitException {
        if (type == null) {
            throw new GitException("Invalid GitObject type");
        }
        this.type = type;
        this.content = content;
        updateSha();
    }

    public GitObject(Type type) throws GitException {
        if (type == null) {
            throw new GitException("Invalid GitObject type");
        }
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
        sha = DigestUtils.sha1Hex(type.toString() + " " + content.length() + '\0' + content);
    }

    public String getSha() {
        return sha;
    }
}