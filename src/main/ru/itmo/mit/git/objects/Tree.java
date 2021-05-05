package ru.itmo.mit.git.objects;

import ru.itmo.mit.git.GitConstants;
import ru.itmo.mit.git.GitException;

import java.util.TreeMap;

public class Tree extends GitObject {
    private String name = "";

    private final TreeMap<String, String> blobsShaToName = new TreeMap<>();
    private final TreeMap<String, String> treesShaToName = new TreeMap<>();
    private final TreeMap<String, String> blobsNameToSha = new TreeMap<>();
    private final TreeMap<String, String> treesNameToSha = new TreeMap<>();

    public Tree() throws GitException {
        super(Type.tree);
    }

    public void updateTreeContent() {
        var entries = new StringBuilder();
        for (var blobEntry : getBlobsShaToName().entrySet()) {
            entries.append(GitConstants.blob).append(" ").append(blobEntry.getKey())
                    .append(' ').append(blobEntry.getValue()).append(System.lineSeparator());
        }
        for (var treeEntry : getTreesShaToName().entrySet()) {
            entries.append(GitConstants.tree).append(" ").append(treeEntry.getKey())
                    .append(' ').append(treeEntry.getValue()).append(System.lineSeparator());
        }
        content = entries.toString();
        updateSha();
    }

    public boolean isEmpty() {
        return blobsNameToSha.isEmpty() && treesNameToSha.isEmpty();
    }

    public void removeBlobByName(String name) {
        var sha = blobsNameToSha.get(name);
        if (sha != null) {
            blobsNameToSha.remove(name);
            blobsShaToName.remove(sha);
        }
    }

    public void removeSubTree(Tree subTree) {
        treesShaToName.remove(subTree.getSha());
        treesNameToSha.remove(subTree.getName());
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean containsBlobByName(String name) {
        return blobsNameToSha.containsKey(name);
    }

    public boolean containsSubTreeByName(String name) {
        return treesNameToSha.containsKey(name);
    }

    public void addBlob(Blob blob) {
        blobsShaToName.put(blob.getSha(), blob.getName());
        blobsNameToSha.put(blob.getName(), blob.getSha());
    }

    public void addBlob(String sha, String filename) {
        blobsShaToName.put(sha, filename);
        blobsNameToSha.put(filename, sha);
    }

    public TreeMap<String, String> getBlobsShaToName() {
        return blobsShaToName;
    }

    public TreeMap<String, String> getTreesShaToName() {
        return treesShaToName;
    }

    public void addSubTree(Tree tree) {
        treesShaToName.put(tree.getSha(), tree.getName());
        treesNameToSha.put(tree.getName(), tree.getSha());
    }

    public void addSubTree(String sha, String filename) {
        treesShaToName.put(sha, filename);
        treesNameToSha.put(filename, sha);
    }

    public String getBlobShaByName(String name) {
        return blobsNameToSha.get(name);
    }

    public String getTreeShaByName(String name) { return treesNameToSha.get(name); }
}
