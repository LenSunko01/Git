package ru.itmo.mit.git;

import ru.itmo.mit.git.context.*;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;

public class Revision {
    private String argument = "";
    private boolean isCommitSha = false;
    private boolean isBranchName = false;
    private boolean isHeadArgument = false;
    private int count = 0;
    private Context context;
    private GitObjectManager objectManager;
    private GitPrettyPrinter writer;
    private GitCommitHistoryService commitHistoryService;
    private GitPathService pathService;
    private GitFileUtils fileUtils;
    private String commitSha = "";

    private void calculateHeadArgument() throws GitException {
        var currentCommitSha = objectManager.getHeadCommitSha();
        if (currentCommitSha.isEmpty()) {
            commitSha = "";
            return;
        }
        var commit = commitHistoryService.getParentCommit(currentCommitSha, count);
        commitSha = commit.getSha();
    }

    private void calculateCommitShaArgument(String sha) {
        commitSha = sha;
    }

    private void calculateBranchNameArgument(String branchName) throws GitException {
        var path = Paths.get(pathService.getPathToHeadsFolder() + File.separator + branchName);
        commitSha = fileUtils.readFromFile(path);
    }

    public Revision(Context context, String revision) throws GitException {
        if (revision == null) {
            return;
        }
        this.context = context;
        objectManager = context.getObjectManager();
        writer = context.getWriter();
        commitHistoryService = context.getCommitHistoryService();
        pathService = context.getPathService();
        fileUtils = context.getFileUtils();
        argument = revision;
        if (revision.startsWith(GitConstants.revisionHeadPrefix)) {
            try {
                var numberParsed = Integer.parseInt(revision.substring(GitConstants.revisionHeadPrefix.length()));
                isHeadArgument = true;
                count = numberParsed;
            } catch (NumberFormatException e) {
                isHeadArgument = false;
            }
        }
        var pathService = context.getPathService();
        var path = Paths.get(pathService.getPathToHeadsFolder() + File.separator + revision);
        if (Files.exists(path)) {
            isBranchName = true;
        }
        path = GitObjectManager.getFullPathToFileBySha(pathService.getPathToCommitsFolder(), revision);
        if (Files.exists(path)) {
            isCommitSha = true;
        }
        if (!isCommitSha && !isBranchName && !isHeadArgument) {
            throw new GitException("Unknown revision");
        }
        if (isCommitSha) {
            calculateCommitShaArgument(revision);
        }
        if (isBranchName) {
            calculateBranchNameArgument(revision);
        }
        if (isHeadArgument) {
            calculateHeadArgument();
        }
    }

    public boolean isHeadArgument() {
        return isHeadArgument;
    }

    public boolean isBranchName() {
        return isBranchName;
    }

    public boolean isCommitSha() {
        return isCommitSha;
    }

    public int getCount() {
        return count;
    }

    public String getArgument() {
        return argument;
    }

    public String getCommitSha() { return commitSha; }

    public static Revision parseRevision(Context context, String revision) throws GitException {
        var result = new Revision(context, revision);
        if (result.getCount() < 0) {
            throw new GitException("Illegal HEAD~ argument");
        }
        return result;
    }
}
