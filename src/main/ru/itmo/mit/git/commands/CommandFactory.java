package ru.itmo.mit.git.commands;

import org.jetbrains.annotations.NotNull;
import ru.itmo.mit.git.GitConstants;
import ru.itmo.mit.git.GitException;
import ru.itmo.mit.git.Revision;
import ru.itmo.mit.git.context.Context;

import java.util.List;
import java.util.Objects;

public class CommandFactory {
    private static final CommandFactory instance = new CommandFactory();
    private CommandFactory() {}

    public static CommandFactory getInstance() {
        return instance;
    }

    public Command createCommand(
            @NotNull Context context,
            @NotNull String command,
            @NotNull List<@NotNull String> arguments
    ) throws GitException {
        switch (command) {
            case GitConstants.INIT:
                if (!arguments.isEmpty()) {
                    throw new GitException("Failed -- INIT does not require any arguments");
                }
                return new InitCommand(context);
            case GitConstants.ADD:
                if (arguments.isEmpty()) {
                    throw new GitException("Failed -- ADD requires file arguments");
                }
                return new AddCommand(context, arguments);
            case GitConstants.COMMIT:
                if (arguments.isEmpty()) {
                    throw new GitException("Failed -- COMMIT requires message");
                }
                return createCommitCommand(context, arguments);
            case GitConstants.RM:
                if (arguments.isEmpty()) {
                    throw new GitException("Failed -- RM requires file arguments");
                }
                return new RmCommand(context, arguments);
            case GitConstants.STATUS:
                if (!arguments.isEmpty()) {
                    throw new GitException("Failed -- STATUS does not require any arguments");
                }
                return new StatusCommand(context);
            case GitConstants.LOG:
                if (arguments.size() > 1) {
                    throw new GitException("Failed -- LOG requires single revision argument or no argument at all");
                }
                return createLogCommand(context, arguments);
            case GitConstants.RESET:
                if (arguments.size() != 1) {
                    throw new GitException("Failed -- RESET requires single revision argument");
                }
                var parsedRevision = Revision.parseRevision(context, arguments.get(0));
                return new ResetCommand(context, parsedRevision);
            case GitConstants.CHECKOUT:
                if (arguments.size() == 0) {
                    throw new GitException("Failed -- CHECKOUT requires arguments");
                }
                if (arguments.get(0).equals("--")) {
                    if (arguments.size() == 1) {
                        throw new GitException("Failed -- CHECKOUT -- requires file arguments");
                    }
                } else if (arguments.size() != 1){
                    throw new GitException("Failed -- CHECKOUT requires single revision argument");
                }
                return createCheckoutCommand(context, arguments);
            case GitConstants.BRANCH_CREATE:
                if (arguments.size() != 1){
                    throw new GitException("Failed -- BRANCH-CREATE requires single branch name argument");
                }
                return new BranchCreateCommand(context, arguments.get(0));
            case GitConstants.BRANCH_REMOVE:
                if (arguments.size() != 1){
                    throw new GitException("Failed -- BRANCH-REMOVE requires single branch name argument");
                }
                return new BranchRemoveCommand(context, arguments.get(0));
            case GitConstants.SHOW_BRANCHES:
                if (!arguments.isEmpty()) {
                    throw new GitException("Failed -- SHOW-BRANCHES requires single branch name argument");
                }
                return new ShowBranchesCommand(context);
        }
        throw new GitException("Invalid command name");
    }

    @NotNull
    private Command createCheckoutCommand(@NotNull Context context, @NotNull List<@NotNull String> arguments) throws GitException {
        if ("--".equals(arguments.get(0))) {
            return new CheckoutFileCommand(context, arguments.subList(1, arguments.size()));
        }
        var parsedRevision = Revision.parseRevision(context, arguments.get(0));
        return new CheckoutRevisionCommand(context, parsedRevision);
    }

    @NotNull
    private LogCommand createLogCommand(@NotNull Context context, @NotNull List<@NotNull String> arguments) throws GitException {
        if (arguments.size() == 1) {
            var parsedRevision = Revision.parseRevision(context, arguments.get(0));
            return new LogCommand(context, parsedRevision);
        }
        return new LogCommand(context);
    }

    @NotNull
    private CommitCommand createCommitCommand(@NotNull Context context, @NotNull List<@NotNull String> arguments) throws GitException {
        if (arguments.isEmpty()) {
            throw new GitException("Commit requires message");
        }
        var message = String.join(" ", arguments);
        return new CommitCommand(context, message);
    }
}
