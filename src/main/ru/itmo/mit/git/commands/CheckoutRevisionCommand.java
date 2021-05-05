package ru.itmo.mit.git.commands;

import ru.itmo.mit.git.*;
import ru.itmo.mit.git.context.*;

import java.io.File;
import java.nio.file.Paths;

public class CheckoutRevisionCommand extends Command {
    private final Revision revision;

    public CheckoutRevisionCommand(Context context, Revision revision) {
        super(context);
        this.revision = revision;
    }

    @Override
    public void execute() throws GitException {
        var sha = revision.getCommitSha();
        if (sha.isEmpty()) {
            throw new GitException("Unknown revision");
        }
        var commit = objectManager.getCommitBySha(sha);
        fileSystemManager.updateFileSystemByCommit(commit);
        if (revision.isBranchName()) {
            fileUtils.writeBranchToHeadFile(pathService.getPathToHeadsFolder() + File.separator + revision.getArgument());
            writer.formattedOutput("Checkout completed successfully");
            return;
        }
        fileUtils.writeDetachedToHeadFile(sha);
        writer.formattedOutput("Checkout completed successfully");
    }
}
