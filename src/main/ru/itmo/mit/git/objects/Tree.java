package ru.itmo.mit.git.objects;

import ru.itmo.mit.git.GitException;
import ru.itmo.mit.git.GitObjectManager;
import ru.itmo.mit.git.GitPathService;

import java.io.File;
import java.util.HashMap;

public class Tree extends GitObject {
    private String name = "";
    private GitPathService pathService = GitPathService.getInstance();
    private GitObjectManager objectManager = GitObjectManager.getInstance();

    private HashMap<String, String> blobsShaToName = new HashMap<>();
    private HashMap<String, String> treesShaToName = new HashMap<>();
    private HashMap<String, String> blobsNameToSha = new HashMap<>();
    private HashMap<String, String> treesNameToSha = new HashMap<>();
    public Tree() {
        super("tree");
    }

    public void updateTreeContent() {
        var entries = new StringBuilder();
        for (var blobEntry : getBlobs().entrySet()) {
            entries.append("blob ").append(blobEntry.getKey())
                    .append(' ').append(blobEntry.getValue()).append(System.lineSeparator());
        }
        for (var treeEntry : getTrees().entrySet()) {
            entries.append("tree ").append(treeEntry.getKey())
                    .append(' ').append(treeEntry.getValue()).append(System.lineSeparator());
        }
        content = entries.toString();
        updateSha();
    }

    public void addFileToTree(String filePath) throws GitException {
        addFileToTree(filePath, this, new File(filePath));
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
                var subTree = addFileToTree(filePath.substring(currentPrefix.length()),
                        objectManager.getTreeBySha(subTreeSha),
                        file);
                tree.addSubTree(subTree);
            } else {
                var subTree = new Tree();
                addFileToTree(filePath.substring(currentPrefix.length()), subTree, file);
                tree.addSubTree(subTree);
            }
        } else {
            var path = pathService.getFullPath(file.getName());
            var blob = objectManager.createBlobFromFile(new File(path.toString()));
            tree.addBlob(blob);
        }
        tree.updateTreeContent();
        objectManager.saveTreeFile(tree);
        return tree;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    public boolean containsBlob(String sha) {
        return blobsShaToName.containsKey(sha);
    }

    public boolean containsSubTree(String sha) {
        return treesShaToName.containsKey(sha);
    }

    public void addBlob(Blob blob) {
        blobsShaToName.put(blob.getSha(), blob.getName());
        blobsNameToSha.put(blob.getName(), blob.getSha());
    }

    public void addBlob(String sha, String filename) {
        blobsShaToName.put(sha, filename);
        blobsNameToSha.put(filename, sha);
    }

    public HashMap<String, String> getBlobs() {
        return blobsShaToName;
    }

    public HashMap<String, String> getTrees() {
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
}
