package ru.itmo.mit.git;

import java.util.Arrays;

public class Main {
    public static void main(String[] args) {
        try {
            if (args.length <= 1) {
                System.out.println("Git command expected.");
                return;
            }
            var argsList = Arrays.asList(args);
            var git = new GitCliImpl(argsList.get(0));
            git.runCommand(argsList.get(1), argsList.subList(2, argsList.size()));
        } catch (GitException e) {
            System.out.println("An error occurred while executing git command.");
            System.out.println(e.getMessage());
        }
    }
}
