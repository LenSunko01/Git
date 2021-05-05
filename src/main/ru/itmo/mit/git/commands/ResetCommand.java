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
        var sha = revision.getCommitSha();
        if (sha.isEmpty()) {
            throw new GitException("Unknown revision");
        }
        executeCommitShaArgument(sha);
        writer.formattedOutput("Reset completed successfully");
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
}
