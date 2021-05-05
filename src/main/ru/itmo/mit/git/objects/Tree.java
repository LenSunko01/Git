package ru.itmo.mit.git.objects;

import ru.itmo.mit.git.GitConstants;
import ru.itmo.mit.git.GitException;

import java.util.TreeMap;

public class Tree extends GitObject {
    private String name = "";

    private final TreeMap<String, String> blobsNameToSha = new TreeMap<>();
    private final TreeMap<String, String> treesNameToSha = new TreeMap<>();

    public Tree() throws GitException {
        super(Type.tree);
    }

    public void updateTreeContent() {
        var entries = new StringBuilder();
        for (var blobEntry : getBlobsNameToSha().entrySet()) {
            entries.append(GitConstants.blob).append(" ").append(blobEntry.getValue())
                    .append(' ').append(blobEntry.getKey()).append(System.lineSeparator());
        }
        for (var treeEntry : getTreesNameToSha().entrySet()) {
            entries.append(GitConstants.tree).append(" ").append(treeEntry.getValue())
                    .append(' ').append(treeEntry.getKey()).append(System.lineSeparator());
        }
        content = entries.toString();
        updateSha();
    }

    public boolean isEmpty() {
        return blobsNameToSha.isEmpty() && treesNameToSha.isEmpty();
    }

    public void removeBlobByName(String name) {
        blobsNameToSha.remove(name);
    }

    public void removeSubTree(Tree subTree) {
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
        blobsNameToSha.put(blob.getName(), blob.getSha());
    }

    public void addBlob(String sha, String filename) {
        blobsNameToSha.put(filename, sha);
    }

    public TreeMap<String, String> getBlobsNameToSha() {
        return blobsNameToSha;
    }

    public TreeMap<String, String> getTreesNameToSha() {
        return treesNameToSha;
    }

    public void addSubTree(Tree tree) {
        treesNameToSha.put(tree.getName(), tree.getSha());
    }

    public void addSubTree(String sha, String filename) {
        treesNameToSha.put(filename, sha);
    }

    public String getBlobShaByName(String name) {
        return blobsNameToSha.get(name);
    }

    public String getTreeShaByName(String name) { return treesNameToSha.get(name); }
}
