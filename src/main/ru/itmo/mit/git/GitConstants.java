package ru.itmo.mit.git;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.regex.Pattern;

public final class GitConstants {
    private GitConstants() {}

    public static final @NotNull String INIT = "init";
    public static final @NotNull String COMMIT = "commit";
    public static final @NotNull String RESET = "reset";
    public static final @NotNull String LOG = "log";
    public static final @NotNull String CHECKOUT = "checkout";
    public static final @NotNull String STATUS = "status";
    public static final @NotNull String ADD = "add";
    public static final @NotNull String RM = "rm";
    public static final @NotNull String BRANCH_CREATE = "branch-create";
    public static final @NotNull String BRANCH_REMOVE = "branch-remove";
    public static final @NotNull String SHOW_BRANCHES = "show-branches";
    public static final @NotNull String MERGE = "merge";
    public static final @NotNull String HELP = "help";

    public static final @NotNull String MASTER = "master";

    public static final String launchScript = "gitCli.sh";
    public static final String gitPrimeFolderName = "gitImpl";
    public static final String gitObjectsFolderName = "objects";
    public static final String gitRefsFolderName = "refs";
    public static final String gitHeadFileName = "HEAD";
    public static final String gitHeadsFolderName = "heads";
    public static final String gitBlobsFolderName = "blobs";
    public static final String gitTreesFolderName = "trees";
    public static final String gitCommitsFolderName = "commits";
    public static final String gitIndexFileName = "index";

    public static final String EMPTY = "";
    public static final String revisionHeadPrefix = "HEAD~";
    public static final String dateFormat = "dd-MM-yyyy HH:mm:ss";
    public static final String COMMIT_DATE = "DATE TIME";
    public static final String headCommitBranchPrefix = "branch ";
    public static final String headCommitDetachedPrefix = "detached ";

    public static final String blob = "blob";
    public static final String tree = "tree";
    public static final String commit = "commit";

    public static final Pattern patternSpace = Pattern.compile(" ");
    public static final Pattern patternSeparator = Pattern.compile(File.separator);
}
