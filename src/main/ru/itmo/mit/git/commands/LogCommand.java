package ru.itmo.mit.git.commands;

import ru.itmo.mit.git.GitException;
import ru.itmo.mit.git.Revision;
import ru.itmo.mit.git.context.Context;

import java.io.File;
import java.nio.file.Paths;
import java.util.ArrayList;

public class LogCommand extends Command {
    private final Revision revision;

    public LogCommand(Context context, Revision revision) {
        super(context);
        this.revision = revision;
    }

    public LogCommand(Context context) {
        super(context);
        revision = null;
    }

    private void executeHeadArgument(int count) throws GitException {
        var currentCommitSha = objectManager.getHeadCommitSha();
        if (currentCommitSha.isEmpty()) {
            writer.formattedOutputLog(new ArrayList<>());
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
