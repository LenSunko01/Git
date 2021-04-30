package ru.itmo.mit.git.testContext;

import ru.itmo.mit.git.context.Context;
import ru.itmo.mit.git.context.GitDateService;

public class ContextTest extends Context {
    private static final Context instanceTest = new ContextTest();
    public static Context getInstance() {
        return instanceTest;
    }

    private ContextTest() {}

    @Override
    public GitDateService getDateService() {
        return new GitDateServiceTest();
    }
}
