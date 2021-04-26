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

    public Git() throws GitException {
        if (Files.exists(pathService.getPathToGitFolder())) {
            var gitIndex = GitIndex.getInstance();
            gitIndex.initialize();
        }
    }
/*
    private List<String> getFilesInIndex() throws GitException {
        List<String> list;
        try {
            list = Files.lines(pathToIndexFile).collect(Collectors.toList());
        } catch (IOException e) {
            throw new GitException("Exception while reading index file", e);
        }
        return list;
    }

    */
/* returns sha-1 of tree builded*//*

    private String buildTree() throws GitException {
        var root = new Node("");
        for (var filePath : getFilesInIndex()) {
            add(root, filePath);
        }
        return createSubTree(root);
    }

    private String getHead() throws GitException {
        List<String> list;
        try {
            list = Files.lines(pathToHeadFile).collect(Collectors.toList());
        } catch (IOException e) {
            throw new GitException("Exception while reading HEAD file", e);
        }
        return list.get(0);
    }

    private String getCommitParentSha() throws GitException {
        var HEAD = getHead();
        List<String> list;
        try {
            list = Files.lines(Paths.get(HEAD)).collect(Collectors.toList());
        } catch (IOException e) {
            throw new GitException("Exception while reading a file", e);
        }
        if (!list.isEmpty()) {
            return list.get(0);
        }
        return null;
    }

    private String getCommitContent(Commit commit) {
        var result = new StringBuilder();
        result.append("tree ").append(commit.getTreeSha()).append(" ");
        if (!(commit.getParentCommitSha() == null)) {
            result.append(commit.getParentCommitSha()).append(" ");
        }
        result.append(commit.getDate()).append(" ").append(commit.getMessage());
        return result.toString();
    }

    private void changeCurrentBranchCommit(String newCommitSha) throws GitException {
        var HEAD = getHead();
        writeToFile(Paths.get(HEAD), newCommitSha);
    }

    private void cleanIndexFile() throws GitException {
        writeToFile(pathToIndexFile, EMPTY);
    }

    private void saveCommitFileIfNotPresent(Commit commit) throws GitException{
        var sha = commit.getSha();
        var path = Paths.get(pathToCommitsFolder + File.separator + sha.substring(0, 2));
        if (!Files.exists(path)) {
            try {
                var filePath = Paths.get(path + File.separator + sha);
                Files.createDirectories(filePath);
                writeToFile(filePath, commit.getContent());
            } catch (IOException e) {
                throw new GitException("Exception while creating a file", e);
            }
        }
    }

    private void cleanIndexFolder() throws GitException {
        try {
            FileUtils.cleanDirectory(new File(pathToIndexFolder.toString()));
        } catch (IOException e) {
            throw new GitException("Exception while cleaning index folder", e);
        }
    }

    private void gitCommit(@NotNull List<@NotNull String> arguments) throws GitException {
        Date date = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        var commit = new Commit(formatter.format(date));

        if (!arguments.isEmpty()) {
            commit.setMessage(arguments.get(0));
        }

        commit.setParent(getCommitParentSha());
        commit.setTreeSha(buildTree());
        commit.setContent(getCommitContent(commit));
        commit.setSha(getHash(commit));
        changeCurrentBranchCommit(commit.getSha());
        cleanIndexFile();
        cleanIndexFolder();
        saveCommitFileIfNotPresent(commit);
        formattedOutput("New commit " + commit.getSha() + " at " + commit.getDate());
    }*/

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
        return null;
    }
}
