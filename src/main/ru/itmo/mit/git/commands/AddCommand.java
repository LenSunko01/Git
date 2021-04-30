package ru.itmo.mit.git.commands;

import ru.itmo.mit.git.GitException;
import ru.itmo.mit.git.context.Context;

import java.util.List;

public class AddCommand extends Command {
    private final List<String> files;
    public AddCommand(Context context, List<String> files) {
        super(context);
        this.files = files;
    }

    @Override
    public void execute() throws GitException {
        for (var file : files) {
            index.addFile(file);
        }
        index.saveIndexTree();
        writer.formattedOutput("Files added successfully");
    }
}
