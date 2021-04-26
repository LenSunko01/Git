package ru.itmo.mit.git;

import java.util.ArrayList;

public class Main {
    public static void main(String[] args) throws GitException {
        var cli = new Git();
        var list = new ArrayList<String>();
        list.add("task4.sh");
//        cli.runCommand(GitConstants.INIT, new ArrayList<>());
//        cli.runCommand(GitConstants.ADD, list);
        var commitList = new ArrayList<String>();
        commitList.add("My message");
        cli.runCommand(GitConstants.COMMIT, commitList);
    }
}
