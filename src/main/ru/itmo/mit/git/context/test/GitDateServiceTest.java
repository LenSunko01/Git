package ru.itmo.mit.git.context.test;

import ru.itmo.mit.git.GitConstants;
import ru.itmo.mit.git.context.GitDateService;

public class GitDateServiceTest extends GitDateService {
    @Override
    public String getDate() {
        return GitConstants.COMMIT_DATE;
    }
}
