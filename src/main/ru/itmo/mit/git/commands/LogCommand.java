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
        writer.formattedOutputLog(commitHistoryService.getCommitHistoryInclusive(currentCommitSha));
    }

    @Override
    public void execute() throws GitException {
        if (revision == null) {
            executeHeadArgument(0);
            return;
        }
        var sha = revision.getCommitSha();
        if (sha.isEmpty()) {
            writer.formattedOutputLog(new ArrayList<>());
            return;
        }
        writer.formattedOutputLog(commitHistoryService.getCommitHistoryInclusive(sha));
    }
}
