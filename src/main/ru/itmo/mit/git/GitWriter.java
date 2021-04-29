package ru.itmo.mit.git;

import ru.itmo.mit.git.objects.Commit;

import java.io.PrintStream;
import java.util.HashSet;
import java.util.List;

public class GitWriter {
    private static final String statusString = "-*-*--*---*----*-----Status-----*----*---*--*-*-";
    private static final String untrackedFiles = "Untracked files:";
    private static final String modifiedFiles = "Modified files:";
    private static final String deletedFiles = "Deleted files:";
    private static final String unstagedFiles = "Files not staged for commit:";
    private static final String stagedFiles = "Files staged for commit:";
    private static final String logString= "-*-*--*---*----*------Log------*----*---*--*-*-";
    private PrintStream outputStream;
    private GitPathService pathService = GitPathService.getInstance();

    private static String getIndents(int count) {
        return "\t".repeat(count);
    }

    private GitWriter() {
        outputStream = System.out;
    }

    public static GitWriter instance = new GitWriter();

    public static GitWriter getInstance() {
        return instance;
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
        if (commits.isEmpty()) {
            return;
        }
        outputStream.println(logString);
        for (var commit : commits) {
            formattedOutputCommit(commit);
        }
        outputStream.println(logString);
    }

    public void formattedOutputStatus(
            HashSet<String> filesUntracked,
            HashSet<String> filesUnstagedModified,
            HashSet<String> filesUnstagedDeleted,
            HashSet<String> filesStagedModified,
            HashSet<String> filesStagedDeleted
    ) {
        outputStream.println(statusString);
        if (!filesUntracked.isEmpty()) {
            outputStream.println(untrackedFiles);
            for (var untrackedFile : filesUntracked) {
                outputStream.print(getIndents(1));
                outputStream.println(untrackedFile);
            }
        }
        if (!(filesUnstagedModified.isEmpty() && filesUnstagedDeleted.isEmpty())) {
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
        if (!(filesStagedModified.isEmpty() && filesStagedDeleted.isEmpty())) {
            outputStream.println(stagedFiles);
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
        outputStream.println(statusString);
    }

    public void setOutputStream(PrintStream printStream) {
        this.outputStream = printStream;
    }
}
