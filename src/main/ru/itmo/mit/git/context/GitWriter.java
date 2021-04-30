package ru.itmo.mit.git.context;

import ru.itmo.mit.git.GitException;
import ru.itmo.mit.git.objects.Commit;

import java.io.PrintStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.List;

public class GitWriter {
    private static final String statusString = "-*-*--*---*----*-----Status-----*----*---*--*-*-";
    private static final String untrackedFiles = "Untracked files:";
    private static final String modifiedFiles = "Modified files:";
    private static final String deletedFiles = "Deleted files:";
    private static final String newFiles = "New files:";
    private static final String unstagedFiles = "Files not staged for commit:";
    private static final String stagedFiles = "Files staged for commit:";
    private static final String logString= "-*-*--*---*----*------Log------*----*---*--*-*-";
    private static final String branchString = "-*-*--*---*----*----Branches----*----*---*--*-*-";
    private PrintStream outputStream;
    private final GitPathService pathService;
    private final GitObjectManager objectManager;

    private static String getIndents(int count) {
        return "\t".repeat(count);
    }

    public GitWriter(GitPathService pathService, GitObjectManager objectManager) {
        this.pathService = pathService;
        this.objectManager = objectManager;
        outputStream = System.out;
    }

    public void formattedOutput(String str) {
        outputStream.println("Git: " + str);
    }

    private void formattedOutputCommit(Commit commit) {
        outputStream.print("* ");
        outputStream.print(commit.getSha());
        outputStream.print(" ");
        outputStream.print(commit.getDate());
        outputStream.print(" ");
        outputStream.println(commit.getMessage());
    }

    public void formattedOutputLog(List<Commit> commits) {
        outputStream.println(logString);
        if (commits.isEmpty()) {
            outputStream.println("No commits yet");
        }
        for (var commit : commits) {
            formattedOutputCommit(commit);
        }
        outputStream.println(logString);
    }

    private void writeCurrentBranch() throws GitException {
        if (objectManager.isDetachedHead()) {
            outputStream.println("Currently in detached HEAD state at: " + objectManager.getHeadCommitSha());
        } else {
            var branchPath = Paths.get(objectManager.getHeadBranchRelativePath());
            outputStream.println("Currently at branch " + pathService.getBranchNameFromPath(branchPath));
        }
    }

    public void formattedOutputStatus(
            HashSet<String> filesUntracked,
            HashSet<String> filesUnstagedModified,
            HashSet<String> filesUnstagedDeleted,
            HashSet<String> filesStagedNew,
            HashSet<String> filesStagedModified,
            HashSet<String> filesStagedDeleted
    ) throws GitException {
        outputStream.println(statusString);
        writeCurrentBranch();
        var noFileChanged = true;
        if (!filesUntracked.isEmpty()) {
            noFileChanged = false;
            outputStream.println(untrackedFiles);
            for (var untrackedFile : filesUntracked) {
                outputStream.print(getIndents(1));
                outputStream.println(untrackedFile);
            }
        }
        if (!(filesUnstagedModified.isEmpty() && filesUnstagedDeleted.isEmpty())) {
            noFileChanged = false;
            outputStream.println(unstagedFiles);
            if (!filesUnstagedModified.isEmpty()) {
                outputStream.print(getIndents(1));
                outputStream.println(modifiedFiles);
                for (var unstagedModified : filesUnstagedModified) {
                    outputStream.print(getIndents(2));
                    outputStream.println(unstagedModified);
                }
            }
            if (!filesUnstagedDeleted.isEmpty()) {
                outputStream.print(getIndents(1));
                outputStream.println(deletedFiles);
                for (var unstagedDeleted : filesUnstagedDeleted) {
                    outputStream.print(getIndents(2));
                    outputStream.println(unstagedDeleted);
                }
            }
        }
        if (!(filesStagedModified.isEmpty() && filesStagedDeleted.isEmpty() && filesStagedNew.isEmpty())) {
            noFileChanged = false;
            outputStream.println(stagedFiles);
            if (!filesStagedNew.isEmpty()) {
                outputStream.print(getIndents(1));
                outputStream.println(newFiles);
                for (var stagedNew : filesStagedNew) {
                    outputStream.print(getIndents(2));
                    outputStream.println(stagedNew);
                }
            }
            if (!filesStagedModified.isEmpty()) {
                outputStream.print(getIndents(1));
                outputStream.println(modifiedFiles);
                for (var stagedModified : filesStagedModified) {
                    outputStream.print(getIndents(2));
                    outputStream.println(stagedModified);
                }
            }
            if (!filesStagedDeleted.isEmpty()) {
                outputStream.print(getIndents(1));
                outputStream.println(deletedFiles);
                for (var stagedDeleted : filesStagedDeleted) {
                    outputStream.print(getIndents(2));
                    outputStream.println(stagedDeleted);
                }
            }
        }
        if (noFileChanged) {
            outputStream.println("Everything up to date");
        }
        outputStream.println(statusString);
    }

    public void setOutputStream(PrintStream printStream) {
        this.outputStream = printStream;
    }

    public void formattedOutputBranches(List<String> branches) throws GitException {
        String currentBranch = null;
        if (!objectManager.isDetachedHead()) {
            currentBranch = objectManager.getHeadBranchName();
        }
        outputStream.println(branchString);
        for (var branch : branches) {
            outputStream.print(getIndents(1));
            if (branch.equals(currentBranch)) {
                outputStream.print("--->");
            }
            outputStream.println(branch);
        }
        outputStream.println(branchString);
    }
}
