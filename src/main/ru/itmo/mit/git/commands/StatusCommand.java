package ru.itmo.mit.git.commands;

import ru.itmo.mit.git.GitException;
import ru.itmo.mit.git.context.Context;

public class StatusCommand extends Command {
    public StatusCommand(Context context) {
        super(context);
    }

    @Override
    public void execute() throws GitException {
        var unstagedDifference = statusManager.compareWorkingTreeAndIndex();
        var stagedDifference = statusManager.compareIndexAndHead();
        var filesUntracked = unstagedDifference.getFilesPresentOnlyInLeftMap();
        var filesUnstagedModified = unstagedDifference.getFilesPresentInBothDifferentSha();
        var filesUnstagedDeleted = unstagedDifference.getFilesPresentOnlyInRightMap();
        var filesStagedNew = stagedDifference.getFilesPresentOnlyInLeftMap();
        var filesStagedModified = stagedDifference.getFilesPresentInBothDifferentSha();
        var filesStagedDeleted = stagedDifference.getFilesPresentOnlyInRightMap();
        writer.formattedOutputStatus(filesUntracked, filesUnstagedModified, filesUnstagedDeleted,
                filesStagedNew, filesStagedModified, filesStagedDeleted);
    }
}
