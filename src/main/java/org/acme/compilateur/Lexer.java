package org.acme.compilateur;

import java.util.ArrayList;
import java.util.List;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class Lexer {
    public static class Token {
        public final String command;
        public final String decision;
        public final int amount;

        public Token(String command, String direction, int amount) {
            this.command = command;
            this.decision = direction;
            this.amount = amount;
        }

        @Override
        public String toString() {
            return String.format("Token[command=%s, direction=%s, steps=%d]", command, decision, amount);
        }
    }

    public List<Token> tokenize(String filePath) throws IOException {
        List<String> lines = Files.readAllLines(Paths.get(filePath));
        List<Token> tokens = new ArrayList<>();
        for (String line : lines) {
            String[] parts = line.split(" ");
            if (parts.length == 3 && parts[0].equalsIgnoreCase("player")) {
                String decision = parts[1];
                int amount;
                try {
                    amount = Integer.parseInt(parts[2]);
                } catch (NumberFormatException e) {
                    throw new IllegalArgumentException("Invalid step count: " + parts[2]);
                }
                tokens.add(new Token(parts[0], decision, amount));
            } else {
                throw new IllegalArgumentException("Invalid command format: " + line);
            }
        }
        return tokens;
    }
}
