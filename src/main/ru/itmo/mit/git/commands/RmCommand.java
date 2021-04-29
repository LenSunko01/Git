package ru.itmo.mit.git.commands;

import ru.itmo.mit.git.GitException;
import ru.itmo.mit.git.GitIndex;

import java.util.List;

public class RmCommand implements Command {
    private final List<String> files;
    private final GitIndex gitIndex = GitIndex.getInstance();

    public RmCommand(List<String> files) {
        this.files = files;
    }

    @Override
    public void execute() throws GitException {
        for (var file : files) {
            gitIndex.deleteFile(file);
        }
        gitIndex.saveIndexTree();
    }
}
