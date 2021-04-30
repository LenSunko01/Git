package ru.itmo.mit.git.context;

import ru.itmo.mit.git.GitConstants;
import ru.itmo.mit.git.GitException;
import ru.itmo.mit.git.objects.Tree;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.HashSet;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class GitStatusManager {
    private final GitObjectManager objectManager;
    private final GitPathService pathService;
    private final GitIndex index;
    public GitStatusManager(GitObjectManager objectManager, GitPathService pathService, GitIndex index) {
        this.objectManager = objectManager;
        this.pathService = pathService;
        this.index = index;
    }

    public class FilesDifference {
        private final HashSet<String> filesPresentOnlyInLeftMap;
        private final HashSet<String> filesPresentOnlyInRightMap;
        private final HashSet<String> filesPresentInBothEqualSha;
        private final HashSet<String> filesPresentInBothDifferentSha;

        public HashSet<String> getFilesPresentInBothDifferentSha() {
            return filesPresentInBothDifferentSha;
        }

        public HashSet<String> getFilesPresentInBothEqualSha() {
            return filesPresentInBothEqualSha;
        }

        public HashSet<String> getFilesPresentOnlyInLeftMap() {
            return filesPresentOnlyInLeftMap;
        }

        public HashSet<String> getFilesPresentOnlyInRightMap() {
            return filesPresentOnlyInRightMap;
        }

        public boolean areEqual() {
            if (!filesPresentOnlyInLeftMap.isEmpty()) {
                return false;
            }
            if (!filesPresentOnlyInRightMap.isEmpty()) {
                return false;
            }
            if (!filesPresentInBothDifferentSha.isEmpty()) {
                return false;
            }
            return true;
        }

        public FilesDifference(HashMap<String, String> leftMap, HashMap<String, String> rightMap) {
            filesPresentOnlyInLeftMap = new HashSet<>();
            filesPresentOnlyInRightMap = new HashSet<>();
            filesPresentInBothEqualSha = new HashSet<>();
            filesPresentInBothDifferentSha = new HashSet<>();
            for (java.util.Map.Entry<String, String> entry : leftMap.entrySet()) {
                var filePath = entry.getKey();
                var fileSha = entry.getValue();
                if (rightMap.containsKey(filePath)) {
                    if (rightMap.get(filePath).equals(fileSha)) {
                        rightMap.remove(filePath);
                        filesPresentInBothEqualSha.add(filePath);
                        continue;
                    }
                    rightMap.remove(filePath);
                    filesPresentInBothDifferentSha.add(filePath);
                    continue;
                }
                filesPresentOnlyInLeftMap.add(filePath);
            }
            for (var entry : rightMap.entrySet()) {
                filesPresentOnlyInRightMap.add(entry.getKey());
            }
        }

    }

    public GitStatusManager.FilesDifference compareWorkingTreeAndIndex() throws GitException {
        var filesOnDisk = getAllFilesOnDisk();
        var filesInIndex = getAllFilesFromTree(index.getRoot(), GitConstants.EMPTY);
        return getFilesDifference(filesOnDisk, filesInIndex);
    }

    public GitStatusManager.FilesDifference compareIndexAndHead() throws GitException {
        var filesInIndex = getAllFilesFromTree(index.getRoot(), GitConstants.EMPTY);
        var headCommitTreeSha = objectManager.getHeadCommitTreeSha();
        var filesInHead = new HashMap<String, String>();
        if (headCommitTreeSha != null) {
            filesInHead = getAllFilesFromTree(objectManager.getTreeBySha(headCommitTreeSha), GitConstants.EMPTY);
        }
        return getFilesDifference(filesInIndex, filesInHead);
    }

    public HashMap<String, String> getAllFilesOnDisk() throws GitException {
        var fileToSha = new HashMap<String, String>();
        try (Stream<Path> paths = Files.walk(pathService.getPathToGitRepository())) {
            var list = paths
                    .filter(file -> !(pathService.fileBelongsToGitFolder(file)) && (new File(file.toString())).isFile())
                    .collect(Collectors.toList());
            for (var file : list) {
                var blob = objectManager.createBlobFromFile(new File(file.toString()));
                fileToSha.put(pathService.getRelativePath(file).toString(), blob.getSha());
            }
        } catch (IOException e) {
            throw new GitException("Exception while reading HEAD file", e);
        }
        return fileToSha;
    }

    public HashMap<String, String> getAllFilesFromTree(Tree root, String currentPath) throws GitException {
        var fileToSha = new HashMap<String, String>();

        for (var blob : root.getBlobsShaToName().entrySet()) {
            if (!currentPath.equals(GitConstants.EMPTY)) {
                fileToSha.put(currentPath + File.separator + blob.getValue(), blob.getKey());
            } else {
                fileToSha.put(blob.getValue(), blob.getKey());
            }
        }
        for (var tree : root.getTreesShaToName().entrySet()) {
            var subTree = objectManager.getTreeBySha(tree.getKey());
            HashMap<String, String> mapFromSubTree;
            if (!currentPath.equals(GitConstants.EMPTY)) {
                mapFromSubTree = getAllFilesFromTree(subTree, currentPath + File.separator + tree.getValue());
            } else {
                mapFromSubTree = getAllFilesFromTree(subTree, tree.getValue());
            }
            fileToSha.putAll(mapFromSubTree);
        }
        return fileToSha;
    }

    public FilesDifference getFilesDifference(HashMap<String, String> leftMap, HashMap<String, String> rightMap) {
        return new FilesDifference(leftMap, rightMap);
    }
}
