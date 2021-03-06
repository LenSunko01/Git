package ru.itmo.mit.git.context;

import ru.itmo.mit.git.GitConstants;
import ru.itmo.mit.git.GitException;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Collectors;

public class GitFileUtils {
    private final GitPathService pathService;
    public GitFileUtils(GitPathService pathService) {
        this.pathService = pathService;
    }

    public void writeBranchToHeadFile(String branch) throws GitException {
        writeToFile(pathService.getPathToHeadFile(), GitConstants.headCommitBranchPrefix + branch);
    }

    public void writeToFile(Path path, String statement) throws GitException {
        try (FileWriter writer = new FileWriter(path.toString())) {
            writer.write(statement);
        } catch (IOException e) {
            throw new GitException("Exception while writing to file", e);
        }
    }

    public String readFromFile(Path path) throws GitException {
        try {
            return Files.lines(path).collect(Collectors.joining(System.lineSeparator()));
        } catch (IOException e) {
            throw new GitException("Exception while reading from file", e);
        }
    }

    public void writeDetachedToHeadFile(String newCommitSha) throws GitException {
        writeToFile(pathService.getPathToHeadFile(), GitConstants.headCommitDetachedPrefix + newCommitSha);
    }
}
