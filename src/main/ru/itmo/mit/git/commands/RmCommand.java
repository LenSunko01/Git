package ru.itmo.mit.git.commands;

import ru.itmo.mit.git.GitException;
import ru.itmo.mit.git.context.Context;

import java.util.ArrayList;
import java.util.List;

public class RmCommand extends Command {
    private final List<String> files;

    public RmCommand(Context context, List<String> files) {
        super(context);
        this.files = files;
    }

    @Override
    public void execute() throws GitException {
        List<String> deletedFiles = new ArrayList<>();
        var success = true;
        for (var file : files) {
            if (index.deleteFile(file)) {
                deletedFiles.add(file);
            } else {
                success = false;
            }
        }
        index.saveIndexTree();
        if (success) {
            writer.formattedOutput("Rm completed successfully");
        } else {
            for (var file : deletedFiles) {
                writer.formattedOutput(file + " deleted");
            }
        }
    }
}
