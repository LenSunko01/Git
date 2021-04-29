package ru.itmo.mit.git.commands;

import ru.itmo.mit.git.GitException;
import ru.itmo.mit.git.GitFileSystemManager;
import ru.itmo.mit.git.GitPathService;

import java.util.List;

public class CheckoutFileCommand implements Command {
    private final GitFileSystemManager fileSystemManager = GitFileSystemManager.getInstance();
    private final GitPathService pathService = GitPathService.getInstance();
    private final List<String> files;

    public CheckoutFileCommand(List<String> files) {
        this.files = files;
    }

    @Override
    public void execute() throws GitException {
        for (var file : files) {
            fileSystemManager.updateWorkingTreeFileFromIndex(pathService.getFullPath(file).toString());
        }
    }
}
