package ru.itmo.mit.git.commands;

import ru.itmo.mit.git.GitException;
import ru.itmo.mit.git.context.Context;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class BranchCreateCommand extends Command {
    private final String branchName;
    BranchCreateCommand(Context context, String branchName) {
        super(context);
        this.branchName = branchName;
    }

    @Override
    public void execute() throws GitException {
        var path = pathService.getPathToBranchByName(branchName);
        if (Files.exists(path)) {
            writer.formattedOutput("Branch '" + branchName + "' already exists");
            return;
        }
        try {
            Files.createFile(path);
        } catch (IOException e) {
            throw new GitException("Exception while creating file", e);
        }
        var currentCommitSha = objectManager.getHeadCommitSha();
        fileUtils.writeToFile(path, currentCommitSha);
        fileUtils.writeBranchToHeadFile(path.toString());
        writer.formattedOutput("Branch created. You are currently at '" + branchName + "'");
    }
}
