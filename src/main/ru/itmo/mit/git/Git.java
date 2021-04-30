package ru.itmo.mit.git;

import org.jetbrains.annotations.NotNull;
import ru.itmo.mit.git.commands.CommandFactory;
import ru.itmo.mit.git.context.*;

import java.io.PrintStream;
import java.nio.file.Files;
import java.util.*;

public class Git implements GitCli {
    private final CommandFactory factory = CommandFactory.getInstance();
    private final Context context;
    private GitPathService pathService;
    private GitWriter writer;
    private GitCommitHistoryService commitHistoryService;
    private GitObjectManager objectManager;
    private GitIndex index;

    private void initialize() {
        pathService = context.getPathService();
        writer = context.getWriter();
        commitHistoryService = context.getCommitHistoryService();
        objectManager = context.getObjectManager();
        index = context.getIndex();
    }

    private void updateGitIndex() throws GitException {
        if (Files.exists(pathService.getPathToGitFolder())) {
            index.initialize();
        }
    }

    public Git() throws GitException {
        context = Context.getInstance();
        initialize();
        updateGitIndex();
    }

    public Git(String workingDirectory) throws GitException {
        context = Context.getInstance();
        initialize();
        pathService.setPathToGitRepository(workingDirectory);
        updateGitIndex();
    }

    public Git(String workingDirectory, Context context) throws GitException {
        this.context = context;
        initialize();
        pathService.setPathToGitRepository(workingDirectory);
        updateGitIndex();
    }

    @Override
    public void runCommand(@NotNull String commandName, @NotNull List<@NotNull String> arguments) throws GitException {
        var writer = context.getWriter();
        if (Objects.equals(commandName, GitConstants.INIT) && !arguments.isEmpty()) {
            writer.formattedOutput("Failed -- INIT does not require any arguments");
            return;
        }
        if (Objects.equals(commandName, GitConstants.ADD) && arguments.isEmpty()) {
            writer.formattedOutput("Failed -- ADD requires file arguments");
            return;
        }
        if (Objects.equals(commandName, GitConstants.RM) && arguments.isEmpty()) {
            writer.formattedOutput("Failed -- RM requires file arguments");
            return;
        }
        if (Objects.equals(commandName, GitConstants.STATUS) && !arguments.isEmpty()) {
            writer.formattedOutput("Failed -- STATUS does not require any arguments");
            return;
        }
        if (Objects.equals(commandName, GitConstants.COMMIT) && (arguments.size() > 1)) {
            writer.formattedOutput("Failed -- COMMIT requires single message or not message at all");
            return;
        }
        if (Objects.equals(commandName, GitConstants.RESET) && (arguments.size() != 1)) {
            writer.formattedOutput("Failed -- RESET requires single revision argument");
            return;
        }
        if (Objects.equals(commandName, GitConstants.LOG) && (arguments.size() > 1)) {
            writer.formattedOutput("Failed -- LOG requires single revision argument or no argument at all");
            return;
        }
        if (Objects.equals(commandName, GitConstants.CHECKOUT)
                && (arguments.size() == 1)
                && ("--".equals(arguments.get(0)))
        ) {
            writer.formattedOutput("Failed -- CHECKOUT -- requires file arguments");
            return;
        }
        if (Objects.equals(commandName, GitConstants.CHECKOUT) && (arguments.size() != 1)) {
            writer.formattedOutput("Failed -- CHECKOUT requires single revision argument");
            return;
        }
        if (Objects.equals(commandName, GitConstants.BRANCH_CREATE) && (arguments.size() != 1)) {
            writer.formattedOutput("Failed -- BRANCH-CREATE requires single branch name argument");
            return;
        }
        if (Objects.equals(commandName, GitConstants.BRANCH_REMOVE) && (arguments.size() != 1)) {
            writer.formattedOutput("Failed -- BRANCH-REMOVE requires single branch name argument");
            return;
        }
        if (Objects.equals(commandName, GitConstants.SHOW_BRANCHES) && !arguments.isEmpty()) {
            writer.formattedOutput("Failed -- SHOW-BRANCHES requires single branch name argument");
            return;
        }
        var command = factory.createCommand(context, commandName, arguments);
        command.execute();
    }

    @Override
    public void setOutputStream(@NotNull PrintStream outputStream) {
        writer.setOutputStream(outputStream);
    }

    @Override
    public @NotNull String getRelativeRevisionFromHead(int n) throws GitException {
        var currentCommitSha = objectManager.getHeadCommitSha();
        if (currentCommitSha.isEmpty()) {
            throw new GitException("No such revision");
        }
        var commit = commitHistoryService.getParentCommit(currentCommitSha, n);
        return commit.getSha();
    }
}
