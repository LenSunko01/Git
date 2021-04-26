package ru.itmo.mit.git.objects;

public class Ref {
    private final String hash;

    public Ref(String hash) {
        this.hash = hash;
    }

    public String getHash() {
        return hash;
    }
}
