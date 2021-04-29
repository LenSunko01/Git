package ru.itmo.mit.git;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.FileUtils;
import org.jetbrains.annotations.NotNull;
import ru.itmo.mit.git.commands.CommandFactory;
import ru.itmo.mit.git.objects.Blob;
import ru.itmo.mit.git.objects.Commit;
import ru.itmo.mit.git.objects.GitObject;
import ru.itmo.mit.git.objects.Tree;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

public class Git implements GitCli {
    private final GitPathService pathService = GitPathService.getInstance();
    private final GitWriter writer = GitWriter.getInstance();
    private final CommandFactory factory = CommandFactory.getInstance();
    private final GitCommitHistoryService commitHistoryService = GitCommitHistoryService.getInstance();
    private final GitObjectManager objectManager = GitObjectManager.getInstance();

    public Git() throws GitException {
        if (Files.exists(pathService.getPathToGitFolder())) {
            var gitIndex = GitIndex.getInstance();
            gitIndex.initialize();
        }
    }

    public Git(String workingDirectory) throws GitException {
        pathService.setPathToGitRepository(workingDirectory);
        if (Files.exists(pathService.getPathToGitFolder())) {
            var gitIndex = GitIndex.getInstance();
            gitIndex.initialize();
        }
    }

    @Override
    public void runCommand(@NotNull String commandName, @NotNull List<@NotNull String> arguments) throws GitException {
        var command = factory.createCommand(commandName, arguments);
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
