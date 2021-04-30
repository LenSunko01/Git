package ru.itmo.mit.git.commands;

import ru.itmo.mit.git.*;
import ru.itmo.mit.git.context.*;

import java.io.File;
import java.nio.file.Paths;

public class ResetCommand extends Command {
    private final Revision revision;
    public ResetCommand(Context context, Revision revision) {
        super(context);
        this.revision = revision;
    }

    @Override
    public void execute() throws GitException {
        if (revision.isHeadArgument()) {
            executeHeadArgument(revision.getCount());
            writer.formattedOutput("Reset completed successfully");
            return;
        }
        if (revision.isBranchName()) {
            executeBranchNameArgument(revision.getArgument());
            writer.formattedOutput("Reset completed successfully");
            return;
        }
        if (revision.isCommitSha()) {
            executeCommitShaArgument(revision.getArgument());
            writer.formattedOutput("Reset completed successfully");
            return;
        }
        throw new GitException("Unknown revision");
    }

    private void executeCommitShaArgument(String commitSha) throws GitException {
        var commit = objectManager.getCommitBySha(commitSha);
        fileSystemManager.updateFileSystemByCommit(commit);
        if (objectManager.isDetachedHead()) {
            fileUtils.writeDetachedToHeadFile(commitSha);
        } else {
            fileUtils.writeToFile(Paths.get(objectManager.getHeadBranchRelativePath()), commitSha);
        }
    }

    private void executeBranchNameArgument(String branchName) throws GitException {
        var path = Paths.get(pathService.getPathToHeadsFolder() + File.separator + branchName);
        var commitSha = fileUtils.readFromFile(path);
        executeCommitShaArgument(commitSha);
    }

    private void executeHeadArgument(int count) throws GitException {
        var currentCommitSha = objectManager.getHeadCommitSha();
        if (currentCommitSha.isEmpty()) {
            return;
        }
        var commit = commitHistoryService.getParentCommit(currentCommitSha, count);
        executeCommitShaArgument(commit.getSha());
    }
}
