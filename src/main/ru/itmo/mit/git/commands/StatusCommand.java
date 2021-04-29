package ru.itmo.mit.git.commands;

import ru.itmo.mit.git.*;

import java.util.HashMap;

public class StatusCommand implements Command {
    private final GitStatusManager statusManager = GitStatusManager.getInstance();
    private final GitObjectManager objectManager = GitObjectManager.getInstance();
    private final GitIndex gitIndex = GitIndex.getInstance();
    private final GitWriter writer = GitWriter.getInstance();
    private final GitFileUtils fileUtils = GitFileUtils.getInstance();

    private GitStatusManager.FilesDifference compareWorkingTreeAndIndex() throws GitException {
        var filesOnDisk = GitStatusManager.getAllFilesOnDisk();
        var filesInIndex = GitStatusManager.getAllFilesFromTree(gitIndex.getRoot(), GitConstants.EMPTY);
        return statusManager.getFilesDifference(filesOnDisk, filesInIndex);
    }

    private GitStatusManager.FilesDifference compareIndexAndHead() throws GitException {
        var filesInIndex = GitStatusManager.getAllFilesFromTree(gitIndex.getRoot(), GitConstants.EMPTY);
        var headCommitTreeSha = objectManager.getHeadCommitTreeSha();
        var filesInHead = new HashMap<String, String>();
        if (headCommitTreeSha != null) {
            filesInHead = GitStatusManager.getAllFilesFromTree(objectManager.getTreeBySha(headCommitTreeSha), GitConstants.EMPTY);
        }
        return statusManager.getFilesDifference(filesInIndex, filesInHead);
    }

    @Override
    public void execute() throws GitException {
        var unstagedDifference = compareWorkingTreeAndIndex();
        var stagedDifference = compareIndexAndHead();
        var filesUntracked = unstagedDifference.getFilesPresentOnlyInLeftMap();
        var filesUnstagedModified = unstagedDifference.getFilesPresentInBothDifferentSha();
        var filesUnstagedDeleted = unstagedDifference.getFilesPresentOnlyInRightMap();
        var filesStagedModified = stagedDifference.getFilesPresentOnlyInLeftMap();
        filesStagedModified.addAll(stagedDifference.getFilesPresentInBothDifferentSha());
        var filesStagedDeleted = stagedDifference.getFilesPresentOnlyInRightMap();
        writer.formattedOutputStatus(filesUntracked, filesUnstagedModified,
                filesUnstagedDeleted, filesStagedModified, filesStagedDeleted);
    }
}
