package ru.itmo.mit.git;

import org.jetbrains.annotations.NotNull;

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

    public static final @NotNull String MASTER = "master";

    public final static String gitPrimeFolderName = "gitImpl";
    public final static String gitObjectsFolderName = "objects";
    public final static String gitRefsFolderName = "refs";
    public final static String gitHeadFileName = "HEAD";
    public final static String gitHeadsFolderName = "heads";
    public final static String gitBlobsFolderName = "blobs";
    public final static String gitTreesFolderName = "trees";
    public final static String gitCommitsFolderName = "commits";
    public final static String gitIndexFileName = "index";

    public final static String EMPTY = "";
    public final static String revisionHeadPrefix = "HEAD~";
    public final static String dateFormat = "dd-MM-yyyy HH:mm:ss";
}
