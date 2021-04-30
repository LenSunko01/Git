package ru.itmo.mit.git.context;

import ru.itmo.mit.git.GitException;
import ru.itmo.mit.git.objects.Commit;
import ru.itmo.mit.git.objects.Tree;

import java.nio.file.Files;

public class GitIndex {
    private Tree root;
    private final GitWriter writer;
    private final GitPathService pathService;
    private final GitObjectManager objectManager;
    private final GitFileUtils fileUtils;
    private final GitTreeManager treeManager;

    public GitIndex(
            GitWriter writer,
            GitPathService pathService,
            GitObjectManager objectManager,
            GitFileUtils fileUtils,
            GitTreeManager treeManager) {
        this.writer = writer;
        this.pathService = pathService;
        this.objectManager = objectManager;
        this.fileUtils = fileUtils;
        this.treeManager = treeManager;
    }

    public String getFileBlobShaByFilePath(String filePath) throws GitException {
        return treeManager.getFileBlobShaByFilePath(root, filePath);
    }

    public void addFile(String filePath) throws GitException {
        var fullPath = pathService.getFullPath(filePath);
        if (!Files.exists(fullPath)) {
            writer.formattedOutput("Failed to add non-existent file " + filePath);
            return;
        }
        treeManager.addFileToTree(root, filePath);
    }

    public boolean deleteFile(String filePath) throws GitException {
        var fullPath = pathService.getFullPath(filePath);
        if (!Files.exists(fullPath) || !isPresent(filePath)) {
            writer.formattedOutput("Failed to delete " + filePath);
            return false;
        }
        treeManager.deleteFileFromTree(root, filePath);
        return true;
    }

    private boolean isPresent(String filePath) throws GitException {
        return treeManager.isPresent(root, filePath);
    }

    public void initialize() throws GitException {
        root = objectManager.getIndexTree();
    }

    public void saveIndexTree() throws GitException {
        fileUtils.writeToFile(pathService.getPathToIndexFile(), root.getSha());
    }

    public Tree getRoot() {
        return root;
    }

    public void updateIndexByCommit(Commit commit) throws GitException {
        root = objectManager.getTreeBySha(commit.getTreeSha());
        saveIndexTree();
    }
}
