package ru.itmo.mit.git.objects;

import ru.itmo.mit.git.GitException;

public class Blob extends GitObject {
    private final String name;

    public Blob(String name, String content) throws GitException {
        super(Type.blob, content);
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
