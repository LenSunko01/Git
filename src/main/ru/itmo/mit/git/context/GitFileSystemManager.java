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
            Files.copy(pathService.getPathToBlobBySha(fileEntry.getValue()), filePath);
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
        try {
            var directoryPaths = Files.list(
                    pathService.getPathToGitRepository()).collect(Collectors.toList());
            for (var file : directoryPaths) {
                if (pathService.fileBelongsToGitFolder(file)) {
                    continue;
                }
                if (Files.isDirectory(file)) {
                    FileUtils.deleteDirectory(new File(file.toString()));
                } else {
                    Files.delete(file);
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
