package ru.itmo.mit.git.commands;

import ru.itmo.mit.git.*;
import ru.itmo.mit.git.objects.Tree;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class InitCommand implements Command {
    @Override
    public void execute() throws GitException {
        var pathService = GitPathService.getInstance();
        var objectManager = GitObjectManager.getInstance();
        var fileUtils = GitFileUtils.getInstance();
        var writer = GitWriter.getInstance();
        if (Files.exists(pathService.getPathToGitFolder())) {
            writer.formattedOutput("Already a git repository");
            return;
        }
        try {
            Files.createDirectory(pathService.getPathToGitFolder());
            Files.createDirectory(pathService.getPathToObjectsFolder());
            Files.createDirectory(pathService.getPathToBlobsFolder());
            Files.createDirectory(pathService.getPathToTreesFolder());
            Files.createDirectory(pathService.getPathToCommitsFolder());
            Files.createDirectories(pathService.getPathToHeadsFolder());
            var pathToMasterBranch = pathService.getPathToHeadsFolder() + File.separator + GitConstants.MASTER;
            Files.createFile(Paths.get(pathToMasterBranch));
            Files.createFile(pathService.getPathToHeadFile());
            fileUtils.writeBranchToHeadFile(pathToMasterBranch);
            Files.createFile(pathService.getPathToIndexFile());

            var indexTree = new Tree();
            objectManager.saveTreeFile(indexTree);
            fileUtils.writeToFile(pathService.getPathToIndexFile(), indexTree.getSha());
            var gitIndex = GitIndex.getInstance();
            gitIndex.initialize();
        } catch (IOException e) {
            /* maybe delete the directory?*/
            throw new GitException("Exception while creating git folder", e);
        }
    }
}
