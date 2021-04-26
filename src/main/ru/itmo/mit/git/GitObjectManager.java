package ru.itmo.mit.git;

import ru.itmo.mit.git.objects.Blob;
import ru.itmo.mit.git.objects.Commit;
import ru.itmo.mit.git.objects.Tree;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.stream.Collectors;

public class GitObjectManager {
    private GitObjectManager() {}
    private static final GitObjectManager instance = new GitObjectManager();
    private static final GitFileUtils fileUtils = GitFileUtils.getInstance();
    private final GitWriter writer = GitWriter.getInstance();
    public static GitObjectManager getInstance() {
        return instance;
    }
    private final GitPathService pathService = GitPathService.getInstance();
    private void parseTreeLine(String line, Tree root) {
        var splitted = Arrays.asList(line.split(" "));
        var sha = splitted.get(1);
        var filename = String.join("", splitted.subList(2, splitted.size()));
        if ("blob".equals(splitted.get(0))) {
            root.addBlob(sha, filename);
        } else {
            root.addSubTree(sha, filename);
        }
    }

    private Tree readTree(Path pathToFile) throws GitException {
        var root = new Tree();
        try {
            Files.lines(pathToFile).forEach(str -> parseTreeLine(str, root));
        } catch (IOException e) {
            throw new GitException("Exception while reading from index file", e);
        }
        root.updateTreeContent();
        return root;
    }

    public Tree getTreeBySha(String sha) throws GitException {
        var pathToFile = pathService.getPathToTreeBySha(sha);
        return readTree(pathToFile);
    }

    private void saveBlobFile(Blob blob) throws GitException {
        var sha = blob.getSha();
        var fileContent = blob.getContent();
        var directory = Paths.get(pathService.getPathToBlobsFolder() +
                        File.separator + sha.substring(0, 2));
        var filePath = Paths.get(directory + File.separator + sha);
        if (!Files.exists(filePath)) {
            try {
                Files.createDirectories(directory);
                Files.createFile(filePath);
                fileUtils.writeToFile(filePath, fileContent);
            } catch (IOException e) {
                throw new GitException("Exception while creating a file", e);
            }
        }
    }

    public Blob createBlobFromFile(File file) throws GitException {
        String fileContent;
        fileContent = fileUtils.readFromFile(file.toPath());
        var blob = new Blob(file.getName(), fileContent);
        saveBlobFile(blob);
        return blob;
    }

    public void saveTreeFile(Tree tree) throws GitException {
        var sha = tree.getSha();
        var directory = Paths.get(pathService.getPathToTreesFolder()
                + File.separator + sha.substring(0, 2));
        var filePath = Paths.get(directory + File.separator + sha);
        if (!Files.exists(filePath)) {
            try {
                Files.createDirectories(directory);
                Files.createFile(filePath);
                fileUtils.writeToFile(filePath, tree.getContent());
            } catch (Exception e) {
                throw new GitException("Exception while creating a file", e);
            }
        }
    }

    public Tree getIndexTree() throws GitException {
        String sha;
        try {
            sha = Files.lines(pathService.getPathToIndexFile()).collect(Collectors.joining());
        } catch (IOException e) {
            throw new GitException("Exception while reading from file", e);
        }
        return getTreeBySha(sha);
    }

    public void saveCommitFile(Commit commit) throws GitException {
        var sha = commit.getSha();
        var directory = Paths.get(pathService.getPathToCommitsFolder()
                + File.separator + sha.substring(0, 2));
        var filePath = Paths.get(directory + File.separator + sha);
        if (!Files.exists(filePath)) {
            try {
                Files.createDirectories(directory);
                Files.createFile(filePath);
                fileUtils.writeToFile(filePath, commit.getContent());
            } catch (IOException e) {
                throw new GitException("Exception while creating a file", e);
            }
        }
    }
}
