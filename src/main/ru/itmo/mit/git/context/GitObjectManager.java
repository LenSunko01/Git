package ru.itmo.mit.git.context;

import ru.itmo.mit.git.GitConstants;
import ru.itmo.mit.git.GitException;
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
    public GitObjectManager(GitFileUtils fileUtils, GitPathService pathService) {
        this.fileUtils = fileUtils;
        this.pathService = pathService;
    }
    private final GitFileUtils fileUtils;
    private final GitPathService pathService;

    public Commit getCommitBySha(String sha) throws GitException {
        var commitContent = getCommitContentBySha(sha);
        var splitted = Arrays.asList(commitContent.split(" "));
        var treeSha = splitted.get(1);
        var parentCommitSha = "";
        var date = "";
        var message = "";
        if (GitConstants.dateFormat.length() < splitted.get(2).length()) {
            parentCommitSha = splitted.get(2);
            date = splitted.get(3) + " " + splitted.get(4);
            if (splitted.size() > 5) {
                message = String.join(" ", splitted.subList(5, splitted.size()));
            }
        } else {
            date = splitted.get(2) + " " + splitted.get(3);
            if (splitted.size() > 4) {
                message = String.join(" ", splitted.subList(4, splitted.size()));
            }
        }
        return new Commit(date, treeSha, parentCommitSha, message);
    }

    public static Path getPathToFolderBySha(Path directory, String sha) {
        return Paths.get(directory + File.separator + sha.substring(0, 2));
    }

    public static Path getFullPathToFileBySha(Path directory, String sha) {
        return Paths.get(getPathToFolderBySha(directory, sha)+ File.separator + sha);
    }

    public String getCommitContentBySha(String sha) throws GitException {
        return fileUtils.readFromFile(getFullPathToFileBySha(pathService.getPathToCommitsFolder(), sha));
    }

    public boolean isDetachedHead() throws GitException {
        var headContent = fileUtils.readFromFile(pathService.getPathToHeadFile());
        return !headContent.startsWith(GitConstants.headCommitBranchPrefix);
    }

    public String getHeadBranchName() throws GitException {
        var relativePath = Paths.get(getHeadBranchRelativePath());
        return relativePath.getFileName().toString();
    }

    public String getHeadBranchRelativePath() throws GitException {
        var headContent = fileUtils.readFromFile(pathService.getPathToHeadFile());
        if (headContent.startsWith(GitConstants.headCommitBranchPrefix)) {
            return headContent.substring(GitConstants.headCommitBranchPrefix.length());
        }
        throw new GitException("No branch in detached HEAD state");
    }

    public String getHeadCommitSha() throws GitException {
        var headContent = fileUtils.readFromFile(pathService.getPathToHeadFile());
        if (headContent.startsWith(GitConstants.headCommitBranchPrefix)) {
            var path = headContent.substring(GitConstants.headCommitBranchPrefix.length());
            return fileUtils.readFromFile(Paths.get(path));
        }
        return headContent.substring(GitConstants.headCommitDetachedPrefix.length());
    }

    public String getHeadCommitTreeSha() throws GitException {
        var headCommitSha = getHeadCommitSha();
        if (headCommitSha.isEmpty()) {
            return null;
        }
        var commitContent = getCommitContentBySha(headCommitSha);
        if (commitContent.isEmpty()) {
            return null;
        }
        return Arrays.asList(GitConstants.patternSpace.split(commitContent)).get(1);
    }

    private void parseTreeLine(String line, Tree root) {
        var splitted = Arrays.asList(GitConstants.patternSpace.split(line));
        var sha = splitted.get(1);
        var filename = String.join("", splitted.subList(2, splitted.size()));
        if (GitConstants.blob.equals(splitted.get(0))) {
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
        var directory = getPathToFolderBySha(pathService.getPathToBlobsFolder(), sha);
        var filePath = getFullPathToFileBySha(pathService.getPathToBlobsFolder(), sha);
        writeContentToFile(fileContent, directory, filePath);
    }

    private void writeContentToFile(String fileContent, Path directory, Path filePath) throws GitException {
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
        var fileContent = fileUtils.readFromFile(file.toPath());
        return new Blob(file.getName(), fileContent);
    }

    public Blob createAndSaveBlobFromFile(File file) throws GitException {
        var blob = createBlobFromFile(file);
        saveBlobFile(blob);
        return blob;
    }

    public void saveTreeFile(Tree tree) throws GitException {
        var sha = tree.getSha();
        var directory = getPathToFolderBySha(pathService.getPathToTreesFolder(), sha);
        var filePath = getFullPathToFileBySha(pathService.getPathToTreesFolder(), sha);
        writeContentToFile(tree.getContent(), directory, filePath);
    }

    public Tree getIndexTree() throws GitException {
        try {
            var sha = Files.lines(pathService.getPathToIndexFile()).collect(Collectors.joining());
            return getTreeBySha(sha);
        } catch (IOException e) {
            throw new GitException("Exception while reading from file", e);
        }
    }

    public void saveCommitFile(Commit commit) throws GitException {
        var sha = commit.getSha();
        var directory = getPathToFolderBySha(pathService.getPathToCommitsFolder(), sha);
        var filePath = getFullPathToFileBySha(pathService.getPathToCommitsFolder(), sha);
        writeContentToFile(commit.getContent(), directory, filePath);
    }
}
