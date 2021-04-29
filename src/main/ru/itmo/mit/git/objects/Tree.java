package ru.itmo.mit.git.objects;

import ru.itmo.mit.git.GitException;
import ru.itmo.mit.git.GitObjectManager;
import ru.itmo.mit.git.GitPathService;

import java.io.File;
import java.util.TreeMap;

public class Tree extends GitObject {
    private String name = "";
    private final GitPathService pathService = GitPathService.getInstance();
    private final GitObjectManager objectManager = GitObjectManager.getInstance();

    private TreeMap<String, String> blobsShaToName = new TreeMap<>();
    private TreeMap<String, String> treesShaToName = new TreeMap<>();
    private TreeMap<String, String> blobsNameToSha = new TreeMap<>();
    private TreeMap<String, String> treesNameToSha = new TreeMap<>();
    public Tree() {
        super("tree");
    }

    public void updateTreeContent() {
        var entries = new StringBuilder();
        for (var blobEntry : getBlobsShaToName().entrySet()) {
            entries.append("blob ").append(blobEntry.getKey())
                    .append(' ').append(blobEntry.getValue()).append(System.lineSeparator());
        }
        for (var treeEntry : getTreesShaToName().entrySet()) {
            entries.append("tree ").append(treeEntry.getKey())
                    .append(' ').append(treeEntry.getValue()).append(System.lineSeparator());
        }
        content = entries.toString();
        updateSha();
    }

    public void deleteFileFromTree(String filePath) throws GitException {
        var pathService = GitPathService.getInstance();
        deleteFileFromTree(filePath, this, new File(pathService.getFullPath(filePath).toString()));
    }

    private static Tree deleteFileFromTree(String filePath, Tree tree, File file) throws GitException {
        var objectManager = GitObjectManager.getInstance();
        var pathService = GitPathService.getInstance();

        var pathTokens = pathService.getPathTokens(filePath);
        var token = pathTokens.get(0);
        if (pathTokens.size() > 1) {
            var currentPrefix = token + File.separator;
            if (tree.treesNameToSha.containsKey(token)) {
                var subTreeSha = tree.treesNameToSha.get(token);
                var subTree = objectManager.getTreeBySha(subTreeSha);
                tree.removeSubTree(subTree);
                var newSubTree = deleteFileFromTree(filePath.substring(currentPrefix.length()), subTree, file);
                if (newSubTree != null) {
                    tree.addSubTree(newSubTree);
                }
            } else {
                var subTree = new Tree();
                var newSubTree = deleteFileFromTree(filePath.substring(currentPrefix.length()), subTree, file);
                if (newSubTree != null) {
                    tree.addSubTree(newSubTree);
                }
            }
        } else {
            tree.removeBlobByName(token);
        }
        tree.updateTreeContent();
        objectManager.saveTreeFile(tree);
        if (tree.isEmpty()) {
            return null;
        }
        return tree;
    }

    private boolean isEmpty() {
        return (blobsNameToSha.isEmpty() && treesNameToSha.isEmpty());
    }

    public void addFileToTree(String filePath) throws GitException {
        var pathService = GitPathService.getInstance();
        addFileToTree(filePath, this, new File(pathService.getFullPath(filePath).toString()));
    }

    private static Tree addFileToTree(String filePath, Tree tree, File file) throws GitException {
        var objectManager = GitObjectManager.getInstance();
        var pathService = GitPathService.getInstance();

        var pathTokens = pathService.getPathTokens(filePath);
        var token = pathTokens.get(0);
        if (pathTokens.size() > 1) {
            var currentPrefix = token + File.separator;
            if (tree.treesNameToSha.containsKey(token)) {
                var subTreeSha = tree.treesNameToSha.get(token);
                var subTree = objectManager.getTreeBySha(subTreeSha);
                tree.removeSubTree(subTree);
                var newSubTree = addFileToTree(filePath.substring(currentPrefix.length()), subTree, file);
                newSubTree.setName(token);
                tree.addSubTree(newSubTree);
            } else {
                var subTree = new Tree();
                var newSubTree = addFileToTree(filePath.substring(currentPrefix.length()), subTree, file);
                newSubTree.setName(token);
                tree.addSubTree(newSubTree);
            }
        } else {
            var blob = objectManager.createAndSaveBlobFromFile(file);
            tree.removeBlobByName(blob.getName());
            tree.addBlob(blob);
        }
        tree.updateTreeContent();
        objectManager.saveTreeFile(tree);
        return tree;
    }

    private void removeBlobByName(String name) {
        var sha = blobsNameToSha.get(name);
        if (sha != null) {
            blobsNameToSha.remove(name);
            blobsShaToName.remove(sha);
        }
    }

    private void removeSubTree(Tree subTree) {
        treesShaToName.remove(subTree.getSha());
        treesNameToSha.remove(subTree.getName());
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean containsBlobBySha(String sha) {
        return blobsShaToName.containsKey(sha);
    }

    public boolean containsBlobByName(String name) {
        return blobsNameToSha.containsKey(name);
    }

    public boolean containsSubTreeBySha(String sha) {
        return treesShaToName.containsKey(sha);
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

    public boolean isPresent(String filePath) throws GitException {
        return getFileBlobShaByFilePath(filePath) != null;
    }

    public String getFileBlobShaByFilePath(String filePath) throws GitException {
        return getFileBlobShaByFilePath(this, filePath);
    }

    private String getFileBlobShaByFilePath(Tree tree, String filePath) throws GitException {
        var objectManager = GitObjectManager.getInstance();
        var pathService = GitPathService.getInstance();

        var pathTokens = pathService.getPathTokens(filePath);
        var token = pathTokens.get(0);
        var currentPrefix = token + File.separator;
        if (pathTokens.size() > 1) {
            if (!tree.containsSubTreeByName(token)) {
                return null;
            }
            var subTreeSha = tree.treesNameToSha.get(token);
            var subTree = objectManager.getTreeBySha(subTreeSha);
            return getFileBlobShaByFilePath(subTree, filePath.substring(currentPrefix.length()));
        }
        if (!tree.containsBlobByName(token)) {
            return null;
        }
        return tree.getBlobShaByName(token);
    }

    private String getBlobShaByName(String name) {
        return blobsNameToSha.get(name);
    }
}
