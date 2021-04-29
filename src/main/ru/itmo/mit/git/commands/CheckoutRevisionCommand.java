package ru.itmo.mit.git.commands;

import ru.itmo.mit.git.*;

import java.io.File;
import java.nio.file.Paths;

public class CheckoutRevisionCommand implements Command {
    private final Revision revision;
    private final GitObjectManager objectManager = GitObjectManager.getInstance();
    private final GitCommitHistoryService commitHistoryService = GitCommitHistoryService.getInstance();
    private final GitFileSystemManager fileSystemManager = GitFileSystemManager.getInstance();
    private final GitFileUtils fileUtils = GitFileUtils.getInstance();
    private final GitPathService pathService = GitPathService.getInstance();


    public CheckoutRevisionCommand(Revision revision) {
        this.revision = revision;
    }

    @Override
    public void execute() throws GitException {
        if (revision.isHeadArgument()) {
            executeHeadArgument(revision.getCount());
            return;
        }
        if (revision.isBranchName()) {
            executeBranchNameArgument(revision.getArgument());
            return;
        }
        if (revision.isCommitSha()) {
            executeCommitShaArgument(revision.getArgument());
            return;
        }
        throw new GitException("Unknown revision");
    }

    private void executeCommitShaArgument(String commitSha) throws GitException {
        var commit = objectManager.getCommitBySha(commitSha);
        fileSystemManager.updateFileSystemByCommit(commit);
        fileUtils.writeDetachedToHeadFile(commitSha);
    }

    private void executeBranchNameArgument(String branchName) throws GitException {
        var path = Paths.get(pathService.getPathToHeadsFolder() + File.separator + branchName);
        var commitSha = fileUtils.readFromFile(path);
        var commit = objectManager.getCommitBySha(commitSha);
        fileSystemManager.updateFileSystemByCommit(commit);
        fileUtils.writeBranchToHeadFile(pathService.getPathToHeadsFolder() + File.separator + branchName);
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
