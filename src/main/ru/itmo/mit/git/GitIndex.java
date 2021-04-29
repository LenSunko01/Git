package ru.itmo.mit.git;

import ru.itmo.mit.git.objects.Commit;
import ru.itmo.mit.git.objects.Tree;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;

public class GitIndex {
    private Tree root;
    private final GitWriter writer = GitWriter.getInstance();
    private final GitPathService pathService = GitPathService.getInstance();
    private final GitObjectManager objectManager = GitObjectManager.getInstance();
    private final GitFileUtils fileUtils = GitFileUtils.getInstance();

    private GitIndex() { }

    public String getFileBlobShaByFilePath(String filePath) throws GitException {
        return root.getFileBlobShaByFilePath(filePath);
    }

    public void addFile(String filePath) throws GitException {
        var fullPath = pathService.getFullPath(filePath);
        if (!Files.exists(fullPath)) {
            writer.formattedOutput("Failed to add non-existent file " + filePath);
            return;
        }
        root.addFileToTree(filePath);
    }

    public void deleteFile(String filePath) throws GitException {
        var fullPath = pathService.getFullPath(filePath);
        if (!Files.exists(fullPath) || !isPresent(filePath)) {
            writer.formattedOutput("Failed to delete file " + filePath);
            return;
        }
        root.deleteFileFromTree(filePath);
    }

    private boolean isPresent(String filePath) throws GitException {
        return root.isPresent(filePath);
    }

    public void initialize() throws GitException {
        root = objectManager.getIndexTree();
    }

    public void saveIndexTree() throws GitException {
        fileUtils.writeToFile(pathService.getPathToIndexFile(), root.getSha());
    }

    private static final GitIndex instance = new GitIndex();
    public static GitIndex getInstance() {
        return instance;
    }

    public Tree getRoot() {
        return root;
    }

    public void updateIndexByCommit(Commit commit) throws GitException {
        root = objectManager.getTreeBySha(commit.getTreeSha());
        saveIndexTree();
    }
}
