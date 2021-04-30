package ru.itmo.mit.git.commands;

import ru.itmo.mit.git.*;
import ru.itmo.mit.git.context.*;
import ru.itmo.mit.git.objects.Tree;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class InitCommand extends Command {
    public InitCommand(Context context) {
        super(context);
    }

    @Override
    public void execute() throws GitException {
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
            index.initialize();
            writer.formattedOutput("Project initialized");
        } catch (IOException e) {
            throw new GitException("Exception while creating git folder", e);
        }
    }
}
