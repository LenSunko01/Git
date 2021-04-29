package ru.itmo.mit.git;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
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

    private final static GitPathService instance = new GitPathService();

    private GitPathService() {
        pathToGitRepository = Paths.get("/home/mario/correct unix"); //Paths.get(System.getProperty("user.dir"));
        initialize();
    }

    public static GitPathService getInstance() {
        return instance;
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
        return Arrays.asList(filePath.split(File.separator));
    }

    public Path getPathToTreeBySha(String sha) {
        return Paths.get(pathToTreesFolder + File.separator + sha.substring(0, 2)
                + File.separator + sha);
    }

    public Path getFullPath(String path) {
        return Paths.get(pathToGitRepository.toString() + File.separator + path);
    }

    public Path getPathToBlobBySha(String sha) {
        return Paths.get(pathToBlobsFolder + File.separator + sha.substring(0, 2)
                + File.separator + sha);
    }

    public Path getRelativePath(Path path) {
        return pathToGitRepository.relativize(path);
    }

    public boolean fileBelongsToGitFolder(Path path) {
        return path.startsWith(pathToGitFolder);
    }
}
