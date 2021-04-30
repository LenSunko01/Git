package ru.itmo.mit.git.context;

import ru.itmo.mit.git.GitException;
import ru.itmo.mit.git.objects.Tree;

import java.io.File;

public class GitTreeManager {
    private final GitPathService pathService;
    private final GitObjectManager objectManager;

    public GitTreeManager(GitPathService pathService, GitObjectManager objectManager) {
        this.pathService = pathService;
        this.objectManager = objectManager;
    }

    public void deleteFileFromTree(Tree tree, String filePath) throws GitException {
        deleteFileFromTree(filePath, tree);
    }

    private Tree deleteFileFromTree(String filePath, Tree tree) throws GitException {
        var pathTokens = pathService.getPathTokens(filePath);
        var token = pathTokens.get(0);
        if (pathTokens.size() > 1) {
            var currentPrefix = token + File.separator;
            if (tree.containsSubTreeByName(token)) {
                var subTreeSha = tree.getTreeShaByName(token);
                var subTree = objectManager.getTreeBySha(subTreeSha);
                subTree.setName(token);
                tree.removeSubTree(subTree);
                var newSubTree = deleteFileFromTree(filePath.substring(currentPrefix.length()), subTree);
                if (newSubTree != null) {
                    newSubTree.setName(token);
                    tree.addSubTree(newSubTree);
                }
            } else {
                var subTree = new Tree();
                var newSubTree = deleteFileFromTree(filePath.substring(currentPrefix.length()), subTree);
                if (newSubTree != null) {
                    newSubTree.setName(token);
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

    public void addFileToTree(Tree tree, String filePath) throws GitException {
        addFileToTree(filePath, tree, new File(pathService.getFullPath(filePath).toString()));
    }

    private Tree addFileToTree(String filePath, Tree tree, File file) throws GitException {
        var pathTokens = pathService.getPathTokens(filePath);
        var token = pathTokens.get(0);
        if (pathTokens.size() > 1) {
            var currentPrefix = token + File.separator;
            if (tree.containsSubTreeByName(token)) {
                var subTreeSha = tree.getTreeShaByName(token);
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

    public boolean isPresent(Tree tree, String filePath) throws GitException {
        return getFileBlobShaByFilePath(tree, filePath) != null;
    }
    public String getFileBlobShaByFilePath(Tree tree, String filePath) throws GitException {
        var pathTokens = pathService.getPathTokens(filePath);
        var token = pathTokens.get(0);
        var currentPrefix = token + File.separator;
        if (pathTokens.size() > 1) {
            if (!tree.containsSubTreeByName(token)) {
                return null;
            }
            var subTreeSha = tree.getTreeShaByName(token);
            var subTree = objectManager.getTreeBySha(subTreeSha);
            return getFileBlobShaByFilePath(subTree, filePath.substring(currentPrefix.length()));
        }
        if (!tree.containsBlobByName(token)) {
            return null;
        }
        return tree.getBlobShaByName(token);
    }
}
