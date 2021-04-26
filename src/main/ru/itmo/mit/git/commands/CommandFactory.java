package ru.itmo.mit.git.commands;

import org.jetbrains.annotations.NotNull;
import ru.itmo.mit.git.GitConstants;
import ru.itmo.mit.git.GitException;

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
        throw new GitException("Invalid command name");
    }
}
