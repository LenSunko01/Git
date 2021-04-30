package ru.itmo.mit.git.commands;

import ru.itmo.mit.git.GitException;
import ru.itmo.mit.git.context.Context;

import java.util.List;

public class CheckoutFileCommand extends Command {
    private final List<String> files;
    public CheckoutFileCommand(Context context, List<String> files) {
        super(context);
        this.files = files;
    }

    @Override
    public void execute() throws GitException {
        var success = true;
        for (var file : files) {
            var ok = fileSystemManager.updateWorkingTreeFileFromIndex(pathService.getFullPath(file).toString());
            if (!ok) {
                success = false;
                writer.formattedOutput("Error - '" + file + "' did not match any file known to git");
            }
        }
        if (success) {
            writer.formattedOutput("Checkout completed successfully");
        }
    }
}
