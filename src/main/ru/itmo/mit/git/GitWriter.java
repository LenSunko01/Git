package ru.itmo.mit.git;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Path;

public class GitWriter {
    private PrintStream outputStream;

    private GitWriter() {
        outputStream = System.out;
    }

    public static GitWriter instance = new GitWriter();

    public static GitWriter getInstance() {
        return instance;
    }

    public void formattedOutput(String str) {
        outputStream.println("Git: " + str);
    }

    public void setOutputStream(PrintStream printStream) {
        this.outputStream = printStream;
    }
}
