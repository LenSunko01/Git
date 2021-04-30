package ru.itmo.mit.git.context;

public class Context {
    private static final Context instance = new Context();
    public static Context getInstance() {
        return instance;
    }

    private final GitIndex index;
    private final GitPrettyPrinter writer;
    private final GitPathService pathService;
    private final GitFileUtils fileUtils;
    private final GitObjectManager objectManager;
    private final GitFileSystemManager fileSystemManager;
    private final GitCommitHistoryService commitHistoryService;
    private final GitStatusManager statusManager;
    private final GitDateService dateService;
    private final GitTreeManager treeManager;

    protected Context() {
        pathService = new GitPathService();
        fileUtils = new GitFileUtils(pathService);
        objectManager = new GitObjectManager(fileUtils, pathService);
        writer = new GitPrettyPrinter(pathService, objectManager);
        treeManager = new GitTreeManager(pathService, objectManager);
        index = new GitIndex(writer, pathService, objectManager, fileUtils, treeManager);
        statusManager = new GitStatusManager(objectManager, pathService, index);
        fileSystemManager = new GitFileSystemManager(objectManager, pathService, index, statusManager);
        commitHistoryService = new GitCommitHistoryService(objectManager);
        dateService = new GitDateService();
    }

    public GitCommitHistoryService getCommitHistoryService() {
        return commitHistoryService;
    }
    public GitFileSystemManager getFileSystemManager() {
        return fileSystemManager;
    }
    public GitFileUtils getFileUtils() {
        return fileUtils;
    }
    public GitIndex getIndex() {
        return index;
    }
    public GitObjectManager getObjectManager() {
        return objectManager;
    }
    public GitPathService getPathService() {
        return pathService;
    }
    public GitStatusManager getStatusManager() {
        return statusManager;
    }
    public GitPrettyPrinter getWriter() { return writer; }
    public GitDateService getDateService() { return dateService; }
}
