package ru.itmo.mit.git.commands;

import ru.itmo.mit.git.*;
import ru.itmo.mit.git.objects.Commit;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;

public class LogCommand implements Command {
    private final Revision revision;
    private final GitObjectManager objectManager = GitObjectManager.getInstance();
    private final GitWriter writer = GitWriter.getInstance();
    private final GitCommitHistoryService commitHistoryService = GitCommitHistoryService.getInstance();
    private final GitFileUtils fileUtils = GitFileUtils.getInstance();
    private final GitPathService pathService = GitPathService.getInstance();

    public LogCommand(Revision revision) {
        this.revision = revision;
    }

    public LogCommand() {
        revision = null;
    }

    private void executeHeadArgument(int count) throws GitException {
        var currentCommitSha = objectManager.getHeadCommitSha();
        if (currentCommitSha.isEmpty()) {
            return;
        }
        var commit = commitHistoryService.getParentCommit(currentCommitSha, count);
        writer.formattedOutputLog(commitHistoryService.getCommitHistoryInclusive(commit.getSha()));
    }

    private void executeCommitShaArgument(String sha) throws GitException {
        writer.formattedOutputLog(commitHistoryService.getCommitHistoryInclusive(sha));
    }

    private void executeBranchNameArgument(String branchName) throws GitException {
        var path = Paths.get(pathService.getPathToHeadsFolder() + File.separator + branchName);
        var commitSha = fileUtils.readFromFile(path);
        if (commitSha.isEmpty()) {
            return;
        }
        writer.formattedOutputLog(commitHistoryService.getCommitHistoryInclusive(commitSha));
    }

    @Override
    public void execute() throws GitException {
        if (revision == null) {
            executeHeadArgument(0);
            return;
        }
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
}
