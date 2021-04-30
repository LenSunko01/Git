package ru.itmo.mit.git.context;

import ru.itmo.mit.git.GitConstants;

import java.text.SimpleDateFormat;
import java.util.Date;

public class GitDateService {
    public GitDateService() {}
    public String getDate() {
        var date = new Date();
        var formatter = new SimpleDateFormat(GitConstants.dateFormat);
        return formatter.format(date);
    }
}
