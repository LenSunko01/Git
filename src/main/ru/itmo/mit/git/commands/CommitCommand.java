package ru.itmo.mit.git.commands;

import ru.itmo.mit.git.*;
import ru.itmo.mit.git.objects.Commit;

import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;

public class CommitCommand implements Command {
    private String message;
    private GitFileUtils fileUtils = GitFileUtils.getInstance();
    private GitPathService pathService = GitPathService.getInstance();
    private GitObjectManager objectManager = GitObjectManager.getInstance();
    private GitWriter writer = GitWriter.getInstance();

    public CommitCommand(String message) {
        this.message = message;
    }

    private String getCommitParentSha() throws GitException {
        return objectManager.getHeadCommitSha();
    }

    private void changeCurrentBranchCommit(String newCommitSha) throws GitException {
        if (objectManager.isDetachedHead()) {
            fileUtils.writeDetachedToHeadFile(newCommitSha);
            return;
        }
        var branchName = objectManager.getHeadBranch();
        fileUtils.writeToFile(Paths.get(branchName), newCommitSha);
    }

    @Override
    public void execute() throws GitException {
        Date date = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat(GitConstants.dateFormat);
        var commit = new Commit(formatter.format(date));
        commit.setMessage(message);

        commit.setParent(getCommitParentSha());
        var gitIndex = GitIndex.getInstance();
        commit.setTreeSha(gitIndex.getRoot().getSha());
        commit.calculateCommitContent();
        changeCurrentBranchCommit(commit.getSha());
        objectManager.saveCommitFile(commit);
        writer.formattedOutput("New commit " + commit.getSha() + " at " + commit.getDate());
    }
}
