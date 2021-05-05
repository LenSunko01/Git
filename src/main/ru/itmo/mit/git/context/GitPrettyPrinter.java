package ru.itmo.mit.git.context;

import ru.itmo.mit.git.GitException;
import ru.itmo.mit.git.objects.Commit;

import java.io.PrintStream;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.List;

public class GitPrettyPrinter {
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

    public GitPrettyPrinter(GitPathService pathService, GitObjectManager objectManager) {
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
        noFileChanged = printUntrackedFiles(filesUntracked, noFileChanged);
        noFileChanged = printUnstagedFiles(filesUnstagedModified, filesUnstagedDeleted, noFileChanged);
        noFileChanged = printStagedFiles(filesStagedNew, filesStagedModified, filesStagedDeleted, noFileChanged);
        if (noFileChanged) {
            outputStream.println("Everything up to date");
        }
        outputStream.println(statusString);
    }

    private boolean printStagedFiles(
            HashSet<String> filesStagedNew,
            HashSet<String> filesStagedModified,
            HashSet<String> filesStagedDeleted,
            boolean noFileChanged
    ) {
        if (!(filesStagedModified.isEmpty() && filesStagedDeleted.isEmpty() && filesStagedNew.isEmpty())) {
            noFileChanged = false;
            outputStream.println(stagedFiles);
            printFiles(filesStagedNew, newFiles);
            printFiles(filesStagedModified, modifiedFiles);
            printFiles(filesStagedDeleted, deletedFiles);
        }
        return noFileChanged;
    }

    private void printFiles(HashSet<String> files, String title) {
        if (!files.isEmpty()) {
            outputStream.print(getIndents(1));
            outputStream.println(title);
            for (var file : files) {
                outputStream.print(getIndents(2));
                outputStream.println(file);
            }
        }
    }

    private boolean printUnstagedFiles(HashSet<String> filesUnstagedModified, HashSet<String> filesUnstagedDeleted, boolean noFileChanged) {
        if (!(filesUnstagedModified.isEmpty() && filesUnstagedDeleted.isEmpty())) {
            noFileChanged = false;
            outputStream.println(unstagedFiles);
            printFiles(filesUnstagedModified, modifiedFiles);
            printFiles(filesUnstagedDeleted, deletedFiles);
        }
        return noFileChanged;
    }

    private boolean printUntrackedFiles(HashSet<String> filesUntracked, boolean noFileChanged) {
        if (!filesUntracked.isEmpty()) {
            noFileChanged = false;
            outputStream.println(untrackedFiles);
            for (var untrackedFile : filesUntracked) {
                outputStream.print(getIndents(1));
                outputStream.println(untrackedFile);
            }
        }
        return noFileChanged;
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

    public void output(String s) {
        outputStream.println(s);
    }
}
