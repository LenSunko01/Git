package ru.itmo.mit.git.objects;

public class Blob extends GitObject {
    private String name;

    public Blob(String name, String content) {
        super("blob", content);
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
