package ru.itmo.mit.git;

import org.apache.commons.io.FileUtils;
import ru.itmo.mit.git.objects.Commit;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.stream.Collectors;

public class GitFileSystemManager {
    private final GitObjectManager objectManager = GitObjectManager.getInstance();
    private final GitPathService pathService = GitPathService.getInstance();
    private final GitIndex gitIndex = GitIndex.getInstance();
    private GitFileSystemManager() {}
    private static final GitFileSystemManager instance = new GitFileSystemManager();
    public static GitFileSystemManager getInstance() {
        return instance;
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

    public void updateWorkingTreeFileFromIndex(String filePath) throws GitException {
        var path = Paths.get(filePath);
        try {
            var blobSha = gitIndex.getFileBlobShaByFilePath(pathService.getRelativePath(Paths.get(filePath)).toString());
            if (Files.exists(path)) {
                Files.delete(path);
            }
            if (blobSha == null) {
                return;
            }
            Files.copy(pathService.getPathToBlobBySha(blobSha), Paths.get(filePath));
        } catch (IOException e) {
            throw new GitException("Exception while updating a file");
        }
    }

    private void updateWorkingTreeByCommit(Commit commit) throws GitException {
        try {
            var directoryPaths = Files.list(pathService.getPathToGitRepository()).collect(Collectors.toList());
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
            e.printStackTrace();
        }

        var filesInTree = GitStatusManager.getAllFilesFromTree(
                objectManager.getTreeBySha(commit.getTreeSha()), GitConstants.EMPTY);
        for (var entryFile : filesInTree.entrySet()) {
            createFileByBlobSha(entryFile);
        }
    }

    public void updateFileSystemByCommit(Commit commit) throws GitException {
        updateWorkingTreeByCommit(commit);
        gitIndex.updateIndexByCommit(commit);
    }
}
