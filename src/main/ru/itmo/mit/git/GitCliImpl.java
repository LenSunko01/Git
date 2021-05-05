package ru.itmo.mit.git;

import org.jetbrains.annotations.NotNull;
import ru.itmo.mit.git.commands.CommandFactory;
import ru.itmo.mit.git.context.*;

import java.io.PrintStream;
import java.nio.file.Files;
import java.util.*;

public class GitCliImpl implements GitCli {
    private final CommandFactory factory = CommandFactory.getInstance();
    private final Context context;
    private GitPathService pathService;
    private GitPrettyPrinter writer;
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

    public GitCliImpl() throws GitException {
        context = Context.getInstance();
        initialize();
        updateGitIndex();
    }

    public GitCliImpl(String workingDirectory) throws GitException {
        context = Context.getInstance();
        initialize();
        pathService.setPathToGitRepository(workingDirectory);
        updateGitIndex();
    }

    public GitCliImpl(String workingDirectory, Context context) throws GitException {
        this.context = context;
        initialize();
        pathService.setPathToGitRepository(workingDirectory);
        updateGitIndex();
    }

    @Override
    public void runCommand(@NotNull String commandName, @NotNull List<@NotNull String> arguments) throws GitException {
        var writer = context.getWriter();
        if (Objects.equals(commandName, GitConstants.HELP)) {
            if (!arguments.isEmpty()) {
                throw new GitException("Failed -- HELP requires no arguments");
            } else {
                writer.output("-----> List of all commands");
                writer.output("./gitCli.sh help");
                writer.output("./gitCli.sh init");
                writer.output("./gitCli.sh add <files>");
                writer.output("./gitCli.sh rm <files>");
                writer.output("./gitCli.sh status");
                writer.output("./gitCli.sh commit <message>");
                writer.output("./gitCli.sh reset <to_revision>");
                writer.output("./gitCli.sh log <from_revision>");
                writer.output("./gitCli.sh checkout <revision>");
                writer.output("./gitCli.sh checkout -- <files>");
                writer.output("./gitCli.sh branch-create <branch>");
                writer.output("./gitCli.sh branch-remove <branch>");
                writer.output("./gitCli.sh show-branches");
            }
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
