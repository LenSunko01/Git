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
        if (Objects.equals(command, GitConstants.INIT)) {
            return new InitCommand(context);
        }
        if (Objects.equals(command, GitConstants.ADD)) {
            return new AddCommand(context, arguments);
        }
        if (Objects.equals(command, GitConstants.COMMIT)) {
            if (arguments.isEmpty()) {
                return new CommitCommand(context, "");
            }
            return new CommitCommand(context, arguments.get(0));
        }
        if (Objects.equals(command, GitConstants.RM)) {
            return new RmCommand(context, arguments);
        }
        if (Objects.equals(command, GitConstants.STATUS)) {
            return new StatusCommand(context);
        }
        if (Objects.equals(command, GitConstants.LOG)) {
            if (arguments.size() == 1) {
                var parsedRevision = Revision.parseRevision(context, arguments.get(0));
                return new LogCommand(context, parsedRevision);
            }
            return new LogCommand(context);
        }
        if (Objects.equals(command, GitConstants.RESET)) {
            var parsedRevision = Revision.parseRevision(context, arguments.get(0));
            return new ResetCommand(context, parsedRevision);
        }
        if (Objects.equals(command, GitConstants.CHECKOUT)) {
            if ("--".equals(arguments.get(0))) {
                return new CheckoutFileCommand(context, arguments.subList(1, arguments.size()));
            }
            var parsedRevision = Revision.parseRevision(context, arguments.get(0));
            return new CheckoutRevisionCommand(context, parsedRevision);
        }
        if (Objects.equals(command, GitConstants.BRANCH_CREATE)) {
            return new BranchCreateCommand(context, arguments.get(0));
        }
        if (Objects.equals(command, GitConstants.BRANCH_REMOVE)) {
            return new BranchRemoveCommand(context, arguments.get(0));
        }
        if (Objects.equals(command, GitConstants.SHOW_BRANCHES)) {
            return new ShowBranchesCommand(context);
        }
        throw new GitException("Invalid command name");
    }
}
