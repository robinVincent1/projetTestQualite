package org.acme.compilateur;

import java.util.List;

public class Parser {

    public static class Command {
        public final String action;
        public final int value;

        public Command(String action, int value) {
            this.action = action;
            this.value = value;
        }
    }

    public static class Program {
        public final List<Command> commands;

        public Program(List<Command> commands) {
            this.commands = commands;
        }
    }

    public Program parse(List<Lexer.Token> tokens) {
        List<Command> commands = tokens.stream()
                .map(token -> new Command(token.decision, token.amount))
                .toList();
        return new Program(commands);
    }
}
