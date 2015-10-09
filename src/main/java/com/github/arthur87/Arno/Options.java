package com.github.arthur87.Arno;

import java.util.Arrays;
import java.util.ListIterator;
import org.apache.commons.lang.math.NumberUtils;

/**
 * Created by asakawa on 15/10/06.
 */
public class Options {
    private static final String PRODUCT_NAME = "arnoc";
    public static final int MAJOR_VERSION = 1;
    public static final int MINOR_VERSION = 0;

    private String inputSql = "";
    private int inputSpeed = 9600;
    private int inputDelay = 1000;
    private String outputFileName = "main.ino";

    public Options(String args[]) {
        StringBuilder inputSql = new StringBuilder("");
        Boolean isInputSql = false;
        String inputSpeed = "9600";
        String inputDelay = "1000";
        String outputFileName = "main.ino";

        ListIterator<String> tokens = Arrays.asList(args).listIterator();
        while (tokens.hasNext()) {
            String token = tokens.next();
            if(token.startsWith("-")) {
                isInputSql = false;
                if(token.equals("-i")) {
                    // SQLの開始
                    isInputSql = true;
                    inputSql.delete(0, inputSql.length());
                }else if(token.equals("-o")) {
                    // 出力ファイル名
                    outputFileName = tokens.next();
                }else if(token.equals("-speed")) {
                    // Serial.beginのspeedを指定する
                    inputSpeed = tokens.next();
                }else if(token.equals("-delay")) {
                    // delayを指定する
                    inputDelay = tokens.next();
                }else if(token.equals("-version")) {
                    // バージョンを表示する
                    this.printVersion();
                }else if(token.equals("-help")) {
                    // ヘルプを表示する
                    this.printUsage();
                }else {
                    // 該当するオプションがないとき
                    this.error("Unknown option: " + token);
                }
            }else if(isInputSql){
                if(inputSql.length() == 0) {
                    inputSql.append(token);
                }else {
                    inputSql.append(" " + token);
                }
            }else {
                this.error("missing argument for " + token);
            }
        }

        if(inputSql.length() == 0) {
            this.error("No input sql");
        }

        this.inputSql = inputSql.toString();
        this.outputFileName = outputFileName;
        if(NumberUtils.isNumber(inputSpeed)) {
            this.inputSpeed = Integer.valueOf(inputSpeed);
        }
        if(NumberUtils.isNumber(inputDelay)) {
            this.inputDelay = Integer.valueOf(inputDelay);
        }
    }

    public String getInputSql() {
        return this.inputSql;
    }

    public String getOutputFileName() {
        return this.outputFileName;
    }

    public int getInputSpeed() {
        return this.inputSpeed;
    }

    public int getInputDelay() {
        return this.inputDelay;
    }

    private String getOptionArg(String option, ListIterator<String> args) {
        String path = option.substring(2);
        if(path.length() != 2) {
            return path;
        }
        return nextArg(option, args);
    }

    private String nextArg(String option, ListIterator<String> args) {
        if(!args.hasNext()) {
            this.error("missing argument for " + option);
        }
        return args.next();
    }

    private void error(String message) {
        System.err.println(message);
        System.exit(0);
    }

    private void printVersion() {
        System.out.printf("%s version \"%d.%d\"", PRODUCT_NAME, MAJOR_VERSION, MINOR_VERSION);
        System.out.println("");
        System.exit(0);
    }

    private void printUsage() {
        System.out.printf("Usage: %s [-options] input", PRODUCT_NAME);
        System.out.println("");
        System.out.println(" -i <arg>  Input SQL.");
        System.out.println(" -o <file>   Write output to <file>.");
        System.out.println(" -speed <arg>  Sets the data rate in bits per second (baud) for serial data transmission.");
        System.out.println(" -delay <arg>  Pauses the program for the amount of time (in miliseconds) specified as parameter.");

        System.out.println(" -version   Shows compiler version and quit.");
        System.out.println(" -help  Prints this message and quit.");
        System.exit(0);
    }
}
