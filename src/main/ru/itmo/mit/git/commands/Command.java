package ru.itmo.mit.git.commands;

import ru.itmo.mit.git.GitException;

public interface Command {
    void execute() throws GitException;
}
