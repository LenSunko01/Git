package ru.itmo.mit.git;

import org.junit.jupiter.api.Test;
import ru.itmo.mit.git.testContext.ContextTest;

/*
 * Т.к. в коммитах при каждом новом запуске получаются разные хеши и
 *   разное время отправки, то в expected логах на их местах используются
 *   COMMIT_HASH и COMMIT_DATE заглушки соответственно
 */
public class GitTest extends AbstractGitTest {
    @Override
    protected GitCli createCli(String workingDir) {
        GitCliImpl git = null;
        try {
            git = new GitCliImpl(workingDir, ContextTest.getInstance());
        } catch (GitException e) {
            e.printStackTrace();
        }
        return git;
    }

    @Override
    protected TestMode testMode() {
        return TestMode.TEST_DATA;
    }

    @Test
    public void testAdd() throws Exception {
        createFile("file.txt", "aaa");
        status();
        add("file.txt");
        status();
        commit("First commit");
        status();
        log();

        check("add.txt");
    }

    @Test
    public void testMultipleCommits() throws Exception {
        String file1 = "file1.txt";
        String file2 = "file2.txt";
        createFile(file1, "aaa");
        createFile(file2, "bbb");
        status();
        add(file1);
        add(file2);
        status();
        rm(file2);
        status();
        commit("Add file1.txt");
        add(file2);
        commit("Add file2.txt");
        status();
        log();

        check("multipleCommits.txt");
    }

    @Test
    public void testCheckoutFile() throws Exception {
        String file = "file.txt";
        createFile(file, "aaa");
        add(file);
        commit("Add file.txt");

        deleteFile(file);
        status();
        checkoutFiles("--", file);
        fileContent(file);
        status();

        createFile(file, "bbb");
        fileContent(file);
        status();
        checkoutFiles("--", file);
        fileContent(file);
        status();

        check("checkoutFile.txt");
    }

    @Test
    public void testReset() throws Exception {
        String file = "file.txt";
        createFile(file, "aaa");
        add(file);
        commit("First commit");

        createFile(file, "bbb");
        add(file);
        commit("Second commit");
        log();

        reset(1);
        fileContent(file);
        log();

        createFile(file, "ccc");
        add(file);
        commit("Third commit");
        log();

        check("reset.txt");
    }

    @Test
    public void testCheckout() throws Exception {
        String file = "file.txt";
        createFile(file, "aaa");
        add(file);
        commit("First commit");

        createFile(file, "bbb");
        add(file);
        commit("Second commit");
        log();

        checkoutRevision(1);
        status();
        log();

        checkoutMaster();
        status();
        log();

        check("checkout.txt");
    }

    @Test
    public void testLogAndStatusEmptyRepository() throws Exception {
        status();
        log();
        check("logStatusEmptyRepository.txt");
    }

    @Test
    public void testMultipleAdd() throws Exception {
        createDirectory("Directory1");
        createDirectory("Directory2");
        createFile("Directory1/file.txt", "aaa");
        createFile("Directory2/file.txt", "abb");
        status();
        add("Directory1/file.txt", "Directory2/file.txt");
        status();
        commit("First commit");
        status();
        log();
        createDirectory("Directory1/Directory3");
        createFile("Directory1/Directory3/newfile.txt", "aaa");
        status();
        add("Directory1/Directory3/newfile.txt");
        status();
        commit("Second commit");
        createFile("file.txt", "bcd");
        status();
        add("file.txt");
        commit("Third commit");
        log();
        check("multipleAdd.txt");
    }

    @Test
    public void testAddTwice() throws Exception {
        createFile("file.txt", "aaa");
        status();
        add("file.txt");
        status();
        add("file.txt");
        status();
        commit("First commit");
        status();
        changeFile("file.txt", "changed content");
        status();
        add("file.txt");
        status();
        changeFile("file.txt", "more change");
        add("file.txt");
        status();
        commit("First commit");
        status();
        log();
        fileContent("file.txt");

        check("addTwice.txt");
    }

    @Test
    public void testInitTwiceBeforeChange() throws Exception {
        init();

        check("initTwiceBeforeChange.txt");
    }

    @Test
    public void testInitMultipleTimesAfterChange() throws Exception {
        createFile("file.txt", "aaa");
        status();
        init();
        status();
        add("file.txt");
        status();
        init();
        status();
        commit("file.txt");
        status();
        init();
        status();

        check("initMultipleTimesAfterChange.txt");
    }

    @Test
    public void commitMultipleTimesNoChange() throws Exception {
        createFile("file.txt", "aaa");
        add("file.txt");
        commit("First commit");
        commit("Second commit");
        commit("First commit");
        log();

        check("commitMultipleTimesNoChange.txt");
    }

    @Test
    public void emptyRepositoryCommit() throws Exception {
        commit("Empty repository commit");
        check("emptyRepositoryCommit.txt");
    }

    @Test
    public void rmFileNotInIndex() throws Exception {
        createFile("file.txt", "aaa");
        add("file.txt");
        rm("file1.txt");
        rm("file.txt", "file1.txt");
        status();
        createFile("file1.txt", "aaa");
        add("file.txt", "file1.txt");
        rm("file1.txt", "file.txt");
        status();
        add("file.txt", "file1.txt");
        rm("file3.txt", "file.txt", "file2.txt", "file1.txt");
        status();
        check("rmFileNotInIndex.txt");
    }

    private void createCommitHistory() throws Exception {
        createFile("first.txt", "aaa");
        add("first.txt");
        commit("Add first.txt");
        createDirectory("Directory1");
        createFile("Directory1/second.txt", "hello");
        createFile("Directory1/third.txt", "world");
        add("Directory1/second.txt", "Directory1/third.txt");
        commit("Add Directory1/second.txt, Directory1/third.txt");
        rm("Directory1/third.txt");
        commit("Delete Directory1/third.txt");
        changeFile("first.txt", "bbb");
        add("first.txt");
        rm("Directory1/second.txt");
        commit("change first.txt content to 'bbb', delete Directory1/second.txt");
        createFile("lastFile.txt", "hello world");
        add("lastFile.txt");
        commit("add lastFile.txt");
    }

    @Test
    public void testResetCommitHash() throws Exception {
        createCommitHistory();
        log();
        resetCommitHash("f4383f9d2e412b65f3cc297bc432182e2f5889e8");
        status();
        fileContent("lastFile.txt");
        fileContent("first.txt");
        resetCommitHash("ac4e74c80dae9effbfc159f59554aac5e8e4ca45");
        status();
        verifyFileDoesNotExist("lastFile.txt");
        fileContent("first.txt");
        fileContent("Directory1/second.txt");
        status();
        check("resetCommitHash.txt");
    }

    @Test
    public void testResetHead() throws Exception {
        createCommitHistory();
        log();
        status();
        resetHead(1);
        status();
        fileContent("first.txt");
        verifyFileDoesNotExist("lastFile.txt");
        resetHead(3);
        status();
        fileContent("first.txt");
        verifyFileDoesNotExist("lastFile.txt");
        check("resetHead.txt");
    }

    @Test
    public void testResetMasterBranch() throws Exception {
        createCommitHistory();
        log();
        createFile("helloFile.txt", "will be removed");
        add("helloFile.txt");
        createFile("worldFile.txt", "will be removed");
        changeFile("lastFile.txt", "new content");
        status();
        resetBranch("master");
        status();
        verifyFileDoesNotExist("helloFile.txt");
        verifyFileExists("worldFile.txt");
        fileContent("lastFile.txt");
        check("resetMasterBranch.txt");
    }

    @Test
    public void testLogDifferentArguments() throws Exception {
        createCommitHistory();
        log();
        logHashCommit("ac4e74c80dae9effbfc159f59554aac5e8e4ca45");
        logHashCommit("d64f11b451192ae897dd8673d8294f4dd3f2b180");
        logHead(1);
        logHead(3);
        logHead(0);
        logHead(4);
        logBranch("master");
        check("testLogDifferentArguments.txt");
    }

    @Test
    public void testCheckoutDifferentArguments() throws Exception {
        createCommitHistory();
        log();
        status();
        checkoutCommitHash("f4383f9d2e412b65f3cc297bc432182e2f5889e8");
        status();
        fileContent("lastFile.txt");
        fileContent("first.txt");
        checkoutCommitHash("ac4e74c80dae9effbfc159f59554aac5e8e4ca45");
        verifyFileDoesNotExist("lastFile.txt");
        fileContent("first.txt");
        fileContent("Directory1/second.txt");
        status();
        checkoutCommitHash("f2b1464c935d73dea9c51029cbc952480b6d6675");
        status();
        fileContent("first.txt");
        verifyFileDoesNotExist("lastFile.txt");
        checkoutMaster();
        status();
        fileContent("first.txt");
        fileContent("lastFile.txt");
        checkoutHead(3);
        verifyFileDoesNotExist("lastFile.txt");
        fileContent("first.txt");
        fileContent("Directory1/third.txt");
        fileContent("Directory1/second.txt");
        status();
        check("checkoutDifferentArguments.txt");
    }

    @Test
    public void testCheckoutFiles() throws Exception {
        createFile("file.txt", "aaa");
        checkoutFiles("--", "file1.txt");
        checkoutFiles("--", "file.txt");
        fileContent("file.txt");
        add("file.txt");
        checkoutFiles("--", "file.txt");
        fileContent("file.txt");
        changeFile("file.txt", "some new content");
        status();
        checkoutFiles("--", "file.txt");
        fileContent("file.txt");
        status();
        commit("file.txt");
        changeFile("file.txt", "here we go again");
        add("file.txt");
        changeFile("file.txt", "more changes");
        checkoutFiles("--", "file.txt");
        fileContent("file.txt");
        check("checkoutFiles.txt");
    }

    @Test
    public void testBranches() throws Exception {
        createFileAndCommit("file1.txt", "aaa");

        createBranch("develop");
        createFileAndCommit("file2.txt", "bbb");

        status();
        log();
        showBranches();
        checkoutMaster();
        status();
        log();

        createBranch("new-feature");
        createFileAndCommit("file3.txt", "ccc");
        status();
        log();

        checkoutBranch("develop");
        status();
        log();

        check("branches.txt");
    }

    @Test
    public void testBranchRemove() throws Exception {
        createFileAndCommit("file1.txt", "aaa");
        createBranch("develop");
        createFileAndCommit("file2.txt", "bbb");
        status();
        checkoutBranch("master");
        status();
        removeBranch("develop");
        showBranches();

        check("branchRemove.txt");
    }

    @Test
    public void testCommitMessage() throws Exception {
        createFile("file.txt", "Initial commit");
        add("file.txt");
        status();
        commit("Initial", "commit");
        check("commitMessage.txt");
    }
}
