package ru.itmo.mit.git.commands;

import ru.itmo.mit.git.GitException;
import ru.itmo.mit.git.context.Context;
import ru.itmo.mit.git.objects.Commit;

import java.nio.file.Paths;

public class CommitCommand extends Command {
    private final String message;

    public CommitCommand(Context context, String message) {
        super(context);
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
        var branchName = objectManager.getHeadBranchRelativePath();
        fileUtils.writeToFile(Paths.get(branchName), newCommitSha);
    }

    @Override
    public void execute() throws GitException {
        if (statusManager.compareIndexAndHead().areEqual()) {
            writer.formattedOutput("Failure - nothing added to commit");
            return;
        }
        var commit = new Commit(dateService.getDate());
        commit.setMessage(message);
        commit.setParent(getCommitParentSha());
        commit.setTreeSha(index.getRoot().getSha());
        commit.calculateCommitContent();
        changeCurrentBranchCommit(commit.getSha());
        objectManager.saveCommitFile(commit);
        writer.formattedOutput("New commit " + commit.getSha() + " at " + commit.getDate());
    }
}
