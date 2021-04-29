package ru.itmo.mit.git;

import java.util.ArrayList;

public class Main {
    public static void main(String[] args) throws GitException {
        var cli = new Git();
        var list = new ArrayList<String>();
        list.add("task4.sh");
        //cli.runCommand(GitConstants.INIT, new ArrayList<>());
        //cli.runCommand(GitConstants.ADD, list);
        list.add("task5.sh");
        //cli.runCommand(GitConstants.ADD, list);
        var commitList = new ArrayList<String>();
        commitList.add("Comment task4.sh");
        //cli.runCommand(GitConstants.COMMIT, commitList);
        list.clear();
        list.add("Directory/task3.sh");
        //cli.runCommand(GitConstants.ADD, list);
        commitList = new ArrayList<>();
        commitList.add("Add Directory/task3.sh");
        //cli.runCommand(GitConstants.COMMIT, commitList);
        var rmList = new ArrayList<String>();
        rmList.add("task4.sh");
        //cli.runCommand(GitConstants.RM, rmList);
        var statusList = new ArrayList<String>();
        cli.runCommand(GitConstants.STATUS, statusList);
//        var logList = new ArrayList<String>();
//        cli.runCommand(GitConstants.LOG, logList);
        list.add("task4.sh");
        //cli.runCommand(GitConstants.ADD, list);
        commitList.clear();
        commitList.add("Add task4.sh");
        //cli.runCommand(GitConstants.COMMIT, commitList);
        list.add("task2.sh");
        //cli.runCommand(GitConstants.ADD, list);
        commitList.clear();
        commitList.add("Add task2.sh");
        //cli.runCommand(GitConstants.COMMIT, commitList);

//        var newAddList = new ArrayList<String>();
//        newAddList.add("task4.sh");
//        cli.runCommand(GitConstants.ADD, newAddList);
//        var newCommitList = new ArrayList<String>();
//        newCommitList.add("Modify task4.sh");
//        cli.runCommand(GitConstants.COMMIT, newCommitList);

        var checkoutList = new ArrayList<String>();
        checkoutList.add("--");
        checkoutList.add("task4.sh");
        cli.runCommand(GitConstants.CHECKOUT, checkoutList);
        statusList = new ArrayList<>();
        cli.runCommand(GitConstants.STATUS, statusList);
        var logList = new ArrayList<String>();
        cli.runCommand(GitConstants.LOG, logList);
    }
}
