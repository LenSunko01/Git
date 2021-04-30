package ru.itmo.mit.git.commands;

import ru.itmo.mit.git.GitException;
import ru.itmo.mit.git.context.Context;

import java.io.IOException;
import java.nio.file.Files;

public class BranchRemoveCommand extends Command {
    private final String branchName;

    public BranchRemoveCommand(Context context, String branchName) {
        super(context);
        this.branchName = branchName;
    }

    @Override
    public void execute() throws GitException {
        var path = pathService.getPathToBranchByName(branchName);
        if (!Files.exists(path)) {
            writer.formattedOutput("Failed as '" + branchName + "' did not match any existing branch");
            return;
        }
        if (!objectManager.isDetachedHead()) {
            var currentBranchName = objectManager.getHeadBranchName();
            if (currentBranchName.equals(branchName)) {
                writer.formattedOutput("Cannot delete checked out branch '" + branchName +"'");
                return;
            }
        }
        try {
            Files.delete(path);
        } catch (IOException e) {
            throw new GitException("Exception while deleting a file");
        }
        writer.formattedOutput("Branch deleted");
    }
}
