package ru.itmo.mit.git.context;

import org.apache.commons.io.FileUtils;
import ru.itmo.mit.git.*;
import ru.itmo.mit.git.objects.Commit;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;
import java.util.stream.Collectors;

import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;

public class GitFileSystemManager {
    private final GitObjectManager objectManager;
    private final GitPathService pathService;
    private final GitIndex index;
    private final GitStatusManager statusManager;

    public GitFileSystemManager(
            GitObjectManager objectManager,
            GitPathService pathService,
            GitIndex index,
            GitStatusManager statusManager
    ) {
        this.objectManager = objectManager;
        this.pathService = pathService;
        this.index = index;
        this.statusManager = statusManager;
    }

    private void createFileByBlobSha(Map.Entry<String, String> fileEntry) throws GitException {
        var filePath = pathService.getFullPath(fileEntry.getKey());
        var file = new File(filePath.toString());
        try {
            Files.createDirectories(file.getParentFile().toPath());
            Files.copy(pathService.getPathToBlobBySha(fileEntry.getValue()), filePath, REPLACE_EXISTING);
        } catch (IOException e) {
            throw new GitException("Exception while updating working tree");
        }
    }

    public boolean updateWorkingTreeFileFromIndex(String filePath) throws GitException {
        var path = Paths.get(filePath);
        try {
            var blobSha = index.getFileBlobShaByFilePath(
                    pathService.getRelativePath(Paths.get(filePath)).toString());
            if (blobSha == null) {
                return false;
            }
            if (Files.exists(path)) {
                Files.delete(path);
            }
            Files.copy(pathService.getPathToBlobBySha(blobSha), Paths.get(filePath));
            return true;
        } catch (IOException e) {
            throw new GitException("Exception while updating a file");
        }
    }

    private void updateWorkingTreeByCommit(Commit commit) throws GitException {
        var filesInIndex = statusManager.getAllFilesFromTree(index.getRoot(), GitConstants.EMPTY);
        try {
            for (var file : filesInIndex.keySet()) {
                Files.delete(pathService.getFullPath(file));
            }
            var directoryPaths = Files.walk(pathService.getPathToGitRepository()).collect(Collectors.toList());
            for (var entry : directoryPaths) {
                var file = new File(entry.toString());
                var list = file.list();
                if (Files.isDirectory(entry) && list != null && list.length == 0) {
                    FileUtils.deleteDirectory(file);
                }
            }
        } catch (IOException e) {
            throw new GitException("Exception while updating working tree", e);
        }

        var filesInTree = statusManager.getAllFilesFromTree(
                objectManager.getTreeBySha(commit.getTreeSha()), GitConstants.EMPTY);
        for (var entryFile : filesInTree.entrySet()) {
            createFileByBlobSha(entryFile);
        }
    }

    public void updateFileSystemByCommit(Commit commit) throws GitException {
        updateWorkingTreeByCommit(commit);
        index.updateIndexByCommit(commit);
    }
}
