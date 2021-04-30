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
                return new InitCommand(context);
            case GitConstants.ADD:
                return new AddCommand(context, arguments);
            case GitConstants.COMMIT:
                return createCommitCommand(context, arguments);
            case GitConstants.RM:
                return new RmCommand(context, arguments);
            case GitConstants.STATUS:
                return new StatusCommand(context);
            case GitConstants.LOG:
                return createLogCommand(context, arguments);
            case GitConstants.RESET:
                var parsedRevision = Revision.parseRevision(context, arguments.get(0));
                return new ResetCommand(context, parsedRevision);
            case GitConstants.CHECKOUT:
                return createCheckoutCommand(context, arguments);
            case GitConstants.BRANCH_CREATE:
                return new BranchCreateCommand(context, arguments.get(0));
            case GitConstants.BRANCH_REMOVE:
                return new BranchRemoveCommand(context, arguments.get(0));
            case GitConstants.SHOW_BRANCHES:
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
    private CommitCommand createCommitCommand(@NotNull Context context, @NotNull List<@NotNull String> arguments) {
        if (arguments.isEmpty()) {
            return new CommitCommand(context, "");
        }
        return new CommitCommand(context, arguments.get(0));
    }
}
