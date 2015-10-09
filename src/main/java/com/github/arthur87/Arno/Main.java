package com.github.arthur87.Arno;

/**
 * Created by asakawa on 15/10/06.
 */
public class Main {
    public static void main(String args[]) {
        Options options = new Options(args);
        new CParser(options.getInputSql(), options.getOutputFileName(), options.getInputSpeed(), options.getInputDelay());
    }
}

