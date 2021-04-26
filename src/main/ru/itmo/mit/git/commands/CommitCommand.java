package ru.itmo.mit.git.commands;

import ru.itmo.mit.git.*;
import ru.itmo.mit.git.objects.Commit;

import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;

public class CommitCommand implements Command {
    private String message = "";
    private GitFileUtils fileUtils = GitFileUtils.getInstance();
    private GitPathService pathService = GitPathService.getInstance();
    private GitObjectManager objectManager = GitObjectManager.getInstance();
    private GitWriter writer = GitWriter.getInstance();

    public CommitCommand(String message) {
        this.message = message;
    }

    private String getCommitParentSha() throws GitException {
        var headPath = fileUtils.readFromFile(pathService.getPathToHeadFile());
        return fileUtils.readFromFile(Paths.get(headPath));
    }

    private String getCommitContent(Commit commit) {
        var result = new StringBuilder();
        result.append("tree ").append(commit.getTreeSha()).append(" ");
        if (!(commit.getParentCommitSha().equals(GitConstants.EMPTY))) {
            result.append(commit.getParentCommitSha()).append(" ");
        }
        result.append(commit.getDate()).append(" ").append(commit.getMessage());
        return result.toString();
    }

    private void changeCurrentBranchCommit(String newCommitSha) throws GitException {
        var headPath = fileUtils.readFromFile(pathService.getPathToHeadFile());
        fileUtils.writeToFile(Paths.get(headPath), newCommitSha);
    }

    @Override
    public void execute() throws GitException {
        Date date = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        var commit = new Commit(formatter.format(date));
        commit.setMessage(message);

        commit.setParent(getCommitParentSha());
        var gitIndex = GitIndex.getInstance();
        commit.setTreeSha(gitIndex.getRoot().getSha());
        commit.setContent(getCommitContent(commit));
        changeCurrentBranchCommit(commit.getSha());
        objectManager.saveCommitFile(commit);
        writer.formattedOutput("New commit " + commit.getSha() + " at " + commit.getDate());
    }
}
