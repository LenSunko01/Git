package ru.itmo.mit.git;

import ru.itmo.mit.git.context.Context;
import ru.itmo.mit.git.context.GitPathService;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;

public class Revision {
    private String argument = "";
    private boolean isCommitSha = false;
    private boolean isBranchName = false;
    private boolean isHeadArgument = false;
    private int count = 0;

    public Revision(Context context, String revision) {
        if (revision == null) {
            return;
        }
        argument = revision;
        if (revision.startsWith(GitConstants.revisionHeadPrefix)) {
            try {
                var numberParsed = Integer.parseInt(revision.substring(GitConstants.revisionHeadPrefix.length()));
                isHeadArgument = true;
                count = numberParsed;
                return;
            } catch (NumberFormatException e) {
                isHeadArgument = false;
            }
        }
        var pathService = context.getPathService();
        var path = Paths.get(pathService.getPathToHeadsFolder() + File.separator + revision);
        if (Files.exists(path)) {
            isBranchName = true;
            return;
        }
        path = Paths.get(pathService.getPathToCommitsFolder() + File.separator
                + revision.substring(0, 2) + File.separator + revision);
        if (Files.exists(path)) {
            isCommitSha = true;
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

    public static Revision parseRevision(Context context, String revision) throws GitException {
        var result = new Revision(context, revision);
        if (result.getCount() < 0) {
            throw new GitException("Illegal HEAD~ argument");
        }
        return result;
    }
}
