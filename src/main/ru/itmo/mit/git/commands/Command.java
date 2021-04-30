package ru.itmo.mit.git.commands;

import ru.itmo.mit.git.GitException;
import ru.itmo.mit.git.context.*;

public abstract class Command {
    protected final GitCommitHistoryService commitHistoryService;
    protected final GitFileSystemManager fileSystemManager;
    protected final GitFileUtils fileUtils;
    protected final GitIndex index;
    protected final GitObjectManager objectManager;
    protected final GitPathService pathService;
    protected final GitStatusManager statusManager;
    protected final GitPrettyPrinter writer;
    protected final GitDateService dateService;

    public Command(Context context) {
        commitHistoryService = context.getCommitHistoryService();
        fileSystemManager = context.getFileSystemManager();
        fileUtils = context.getFileUtils();
        index = context.getIndex();
        objectManager = context.getObjectManager();
        pathService = context.getPathService();
        statusManager = context.getStatusManager();
        writer = context.getWriter();
        dateService = context.getDateService();
    }
    public abstract void execute() throws GitException;
}
