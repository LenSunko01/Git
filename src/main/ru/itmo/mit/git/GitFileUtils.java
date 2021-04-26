package ru.itmo.mit.git;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Collectors;

public class GitFileUtils {
    private final GitPathService pathService = GitPathService.getInstance();

    private static final GitFileUtils instance = new GitFileUtils();
    public static GitFileUtils getInstance() {
        return instance;
    }
    private GitFileUtils() {}

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
}
