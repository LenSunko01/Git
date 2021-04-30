package ru.itmo.mit.git;

import java.util.Arrays;

public class Main {
    public static void main(String[] args) {
        try {
            if (args.length == 0) {
                System.out.println("Git command expected.");
                return;
            }
            var git = new Git();
            var argsList = Arrays.asList(args);
            git.runCommand(argsList.get(0), argsList.subList(1, argsList.size()));
        } catch (GitException e) {
            System.out.println("An error occurred while executing git command.");
            e.printStackTrace();
        }
    }
}
