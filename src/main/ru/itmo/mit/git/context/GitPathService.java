package ru.itmo.mit.git.context;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

import static ru.itmo.mit.git.GitConstants.*;

public class GitPathService {
    private Path pathToGitRepository;
    private Path pathToGitFolder;
    private Path pathToHeadsFolder;
    private Path pathToHeadFile;
    private Path pathToIndexFile;
    private Path pathToObjectsFolder;
    private Path pathToBlobsFolder;
    private Path pathToTreesFolder;
    private Path pathToCommitsFolder;
    private Path pathToLaunchScript;

    public GitPathService() {
        pathToGitRepository = Paths.get(System.getProperty("user.dir"));
        initialize();
    }

    private void initialize() {
        pathToGitFolder = getFullPath(gitPrimeFolderName);
        pathToObjectsFolder = Paths.get(pathToGitFolder + File.separator + gitObjectsFolderName);
        pathToBlobsFolder = Paths.get(pathToObjectsFolder + File.separator + gitBlobsFolderName);
        pathToTreesFolder = Paths.get(pathToObjectsFolder + File.separator + gitTreesFolderName);
        pathToCommitsFolder = Paths.get(pathToObjectsFolder + File.separator + gitCommitsFolderName);
        pathToHeadsFolder = Paths.get(pathToGitFolder + File.separator + gitRefsFolderName +
                File.separator + gitHeadsFolderName);
        pathToHeadFile = Paths.get(pathToGitFolder + File.separator + gitHeadFileName);
        pathToIndexFile = Paths.get(pathToGitFolder + File.separator + gitIndexFileName);
        pathToLaunchScript = getFullPath(launchScript);
    }

    public void setPathToGitRepository(String path) {
        pathToGitRepository = Paths.get(path);
        initialize();
    }

    public Path getPathToHeadsFolder() {
        return pathToHeadsFolder;
    }

    public Path getPathToHeadFile() {
        return pathToHeadFile;
    }

    public Path getPathToIndexFile() {
        return pathToIndexFile;
    }

    public Path getPathToBlobsFolder() {
        return pathToBlobsFolder;
    }

    public Path getPathToTreesFolder() {
        return pathToTreesFolder;
    }

    public Path getPathToCommitsFolder() {
        return pathToCommitsFolder;
    }

    public Path getPathToGitRepository() {
        return pathToGitRepository;
    }

    public Path getPathToGitFolder() {
        return pathToGitFolder;
    }

    public Path getPathToObjectsFolder() {
        return pathToObjectsFolder;
    }

    public List<String> getPathTokens(String filePath) {
        return Arrays.asList(patternSeparator.split(filePath));
    }

    public Path getPathToTreeBySha(String sha) {
        return GitObjectManager.getFullPathToFileBySha(pathToTreesFolder, sha);
    }

    public Path getFullPath(String path) {
        return Paths.get(pathToGitRepository.toString() + File.separator + path);
    }

    public Path getPathToBlobBySha(String sha) {
        return GitObjectManager.getFullPathToFileBySha(pathToBlobsFolder, sha);
    }

    public String getBranchNameFromPath(Path path) {
        return path.getFileName().toString();
    }

    public Path getRelativePath(Path path) {
        return pathToGitRepository.relativize(path);
    }

    public boolean fileBelongsToGitFolder(Path path) {
        return path.startsWith(pathToGitFolder);
    }

    public Path getPathToBranchByName(String branchName) {
        return Paths.get(pathToHeadsFolder + File.separator + branchName);
    }

    public boolean fileIsLaunchScript(Path path) {
        return path.startsWith(pathToLaunchScript);
    }

    public boolean fileIsGitRepository(Path path) {
        return path.equals(pathToGitRepository);
    }
}
