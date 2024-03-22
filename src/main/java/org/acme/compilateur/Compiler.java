package org.acme.compilateur;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class Compiler {
    public static void main(String[] args) {
        String inputFilePath = "src/main/resources/instructions.txt";
        String outputFilePath = "src/main/resources/PlayerController.java";

        Lexer lexer = new Lexer();
        Parser parser = new Parser();
        CodeGenerator codeGenerator = new CodeGenerator();

        try {
            // Lexer : Tokenize input file
            var tokens = lexer.tokenize(inputFilePath);

            // Parser : Parse tokens into program structure
            var program = parser.parse(tokens);

            // CodeGenerator : Generate Java code from program structure
            System.out.println("1");
            codeGenerator.generateCode(program, outputFilePath);
            System.out.println("2");
            System.out.println("Compilation successful. Generated " + outputFilePath);
        } catch (IOException e) {
            System.err.println("Error reading or writing files: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Compilation error: " + e.getMessage());
        }
    }

    private static void testFileCreation(String testFilePath) {
        try {
            Files.writeString(Paths.get(testFilePath), "public class TestFile {}");
            System.out.println("Test file creation successful: " + testFilePath);
        } catch (IOException e) {
            System.err.println("Test file creation failed: " + e.getMessage());
        }
    }
}
