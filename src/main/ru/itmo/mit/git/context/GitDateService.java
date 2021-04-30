package ru.itmo.mit.git.context;

import ru.itmo.mit.git.GitConstants;

import java.text.SimpleDateFormat;
import java.util.Date;

public class GitDateService {
    public GitDateService() {}
    public String getDate() {
        Date date = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat(GitConstants.dateFormat);
        return formatter.format(date);
    }
}
