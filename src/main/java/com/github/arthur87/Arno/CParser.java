package com.github.arthur87.Arno;

import net.sf.jsqlparser.expression.operators.relational.*;
import net.sf.jsqlparser.expression.*;
import org.apache.commons.lang.math.NumberUtils;
import java.util.Arrays;
import java.io.File;
import java.io.FileWriter;
import java.io.BufferedWriter;
import java.io.PrintWriter;
import java.io.IOException;

/**
 * Created by asakawa on 15/10/09.
 */
public class CParser {
    public CParser(String sql, String outputFileName, int speed, int delay) {
        CSQL csql = new CSQL();
        csql.parser(sql);

        if(!csql.getTable().equals("arduino")) {
            System.err.println("Unknown table: " + csql.getTable());
            System.exit(0);
        }

        int digitalPins[] = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
        int analogPins[] = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};

        String columns[] = {
            "din1", "din2", "din3", "din4", "din5", "din6", "din7", "din8", "din9", "din10",
            "din11", "din12", "din13", "din14", "din15", "din16", "din17", "din18", "din19", "din20",
            "ain1", "ain2", "ain3", "ain4", "ain5", "ain6", "ain7", "ain8", "ain9", "ain10",
            "ain11", "ain12", "ain13", "ain14", "ain15", "ain16", "ain17", "ain18", "ain19", "ain20"
        };

        for(String field: csql.getFields()) {
            // カラム名が存在するか確認する
            if(!Arrays.asList(columns).contains(field)) {
                System.err.println("Unknown column: " + field);
                System.exit(0);
            }

            int pinIndex = Integer.valueOf(field.substring(3));
            if(field.startsWith("din")) {
                digitalPins[pinIndex] = 1;
            }else {
                analogPins[pinIndex] = 1;
            }
        }

        for(Expression expression: csql.getConditions()) {
            // WHERE句のカラム名が存在するか確認する
            String left = "";
            String right = "";
            if(expression instanceof EqualsTo) {
                left = ((EqualsTo)expression).getLeftExpression().toString();
                right = ((EqualsTo)expression).getRightExpression().toString();
            }else if(expression instanceof GreaterThan) {
                left = ((GreaterThan)expression).getLeftExpression().toString();
                right = ((GreaterThan)expression).getRightExpression().toString();
            }else if(expression instanceof GreaterThanEquals) {
                left = ((GreaterThanEquals)expression).getLeftExpression().toString();
                right = ((GreaterThanEquals)expression).getRightExpression().toString();
            }else if(expression instanceof MinorThan) {
                left = ((MinorThan)expression).getLeftExpression().toString();
                right = ((MinorThan)expression).getRightExpression().toString();
            }else if(expression instanceof MinorThanEquals) {
                left = ((MinorThanEquals)expression).getLeftExpression().toString();
                right = ((MinorThanEquals)expression).getRightExpression().toString();
            }else if(expression instanceof NotEqualsTo) {
                left = ((NotEqualsTo)expression).getLeftExpression().toString();
                right = ((NotEqualsTo)expression).getRightExpression().toString();
            }

            if(!Arrays.asList(columns).contains(left)) {
                System.err.println("Unknown column: " + left);
                System.exit(0);
            }

            int pinIndex = Integer.valueOf(left.substring(3));
            if(left.startsWith("din")) {
                digitalPins[pinIndex] = 1;
            }else {
                analogPins[pinIndex] = 1;
            }

            if(!NumberUtils.isNumber(right)) {
                System.err.println("missing value for " + right);
                System.exit(0);
            }
        }

        StringBuffer condition = new StringBuffer(" ");
        for(String token: csql.getWhere().split(" ")) {
            // WHERE句のSQL文法をC言語文法に書き換える
            if(token.equals("=")) {
                condition.append("== ");
            }else if(token.equals("AND")) {
                condition.append("&& ");
            }else if(token.equals("OR")) {
                condition.append("|| ");
            }else {
                condition.append(token + " ");
            }
        }


        try {
            File file = new File(outputFileName);
            PrintWriter printWriter = new PrintWriter(new BufferedWriter(new FileWriter(file)));

            // setup
            printWriter.println("void setup() {");
            printWriter.println(String.format("  Serial.begin(%d);", speed));
            for(int i = 0; i < digitalPins.length; i++) {
                if(digitalPins[i] == 1)
                    printWriter.println(String.format("  pinMode(%d, INPUT);", i));
            }
            printWriter.println("}");
            printWriter.println("");

            // loop
            printWriter.println("void loop() {");
            for(int i = 0; i < digitalPins.length; i++) {
                if(digitalPins[i] == 1)
                    printWriter.println(String.format("  int din%d = digitalRead(%d);", i, i));
            }
            for(int i = 0; i < analogPins.length; i++) {
                if(analogPins[i] == 1)
                    printWriter.println(String.format("  int ain%d = analogRead(%d);", i, i));
            }

            // if
            printWriter.println(String.format("  if(%s) {", condition.toString()));
            printWriter.println("    Serial.print(\"[ \");");
            for(String field: csql.getFields()) {
                printWriter.println(String.format("    Serial.print(%s);", field));
                printWriter.println("    Serial.print(\" \");");
            }
            printWriter.println("    Serial.print(\"]\");");
            printWriter.println(String.format("    delay(%d);", delay));

            printWriter.println("  }");
            printWriter.println("}");

            printWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
