package ru.itmo.mit.git.commands;

import ru.itmo.mit.git.GitException;
import ru.itmo.mit.git.context.Context;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ShowBranchesCommand extends Command {
    ShowBranchesCommand(Context context) {
        super(context);
    }

    @Override
    public void execute() throws GitException {
        try (Stream<Path> paths = Files.walk(pathService.getPathToHeadsFolder())) {
            var list = paths
                    .filter(file -> (new File(file.toString())).isFile())
                    .map(file -> file.getFileName().toString())
                    .collect(Collectors.toList());
            writer.formattedOutputBranches(list);
        } catch (IOException e) {
            throw new GitException("Exception while reading files");
        }
    }
}
