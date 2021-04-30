package ru.itmo.mit.git.context.test;

import ru.itmo.mit.git.context.Context;
import ru.itmo.mit.git.context.GitDateService;

public class ContextTest extends Context {
    private static final Context instanceTest = new ContextTest();
    public static Context getInstance() {
        return instanceTest;
    }
    @Override
    public GitDateService getDateService() {
        return new GitDateServiceTest();
    }
}
