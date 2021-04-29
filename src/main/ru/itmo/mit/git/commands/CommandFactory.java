package ru.itmo.mit.git.commands;

import org.jetbrains.annotations.NotNull;
import ru.itmo.mit.git.GitConstants;
import ru.itmo.mit.git.GitException;
import ru.itmo.mit.git.Revision;

import java.util.List;
import java.util.Objects;

public class CommandFactory {
    private static final CommandFactory instance = new CommandFactory();
    private CommandFactory() {}

    public static CommandFactory getInstance() {
        return instance;
    }

    public Command createCommand(@NotNull String command, @NotNull List<@NotNull String> arguments) throws GitException {
        if (Objects.equals(command, GitConstants.INIT)) {
            return new InitCommand();
        }
        if (Objects.equals(command, GitConstants.ADD)) {
            return new AddCommand(arguments);
        }
        if (Objects.equals(command, GitConstants.COMMIT)) {
            if (arguments.isEmpty()) {
                return new CommitCommand("");
            }
            return new CommitCommand(arguments.get(0));
        }
        if (Objects.equals(command, GitConstants.RM)) {
            return new RmCommand(arguments);
        }
        if (Objects.equals(command, GitConstants.STATUS)) {
            return new StatusCommand();
        }
        if (Objects.equals(command, GitConstants.LOG)) {
            if (arguments.size() == 1) {
                var parsedRevision = Revision.parseRevision(arguments.get(0));
                return new LogCommand(parsedRevision);
            }
            return new LogCommand();
        }
        if (Objects.equals(command, GitConstants.RESET)) {
            var parsedRevision = Revision.parseRevision(arguments.get(0));
            return new ResetCommand(parsedRevision);
        }
        if (Objects.equals(command, GitConstants.CHECKOUT)) {
            if ("--".equals(arguments.get(0))) {
                return new CheckoutFileCommand(arguments.subList(1, arguments.size()));
            }
            var parsedRevision = Revision.parseRevision(arguments.get(0));
            return new CheckoutRevisionCommand(parsedRevision);
        }
        throw new GitException("Invalid command name");
    }
}
