package org.acme.game.compilateur;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;

import org.acme.compilateur.Lexer;
import org.acme.compilateur.Parser;
import org.junit.jupiter.api.Test;
import java.util.Arrays;
import java.util.List;

public class ParserTest {

    @Test
    public void testParseCommands() {
        Parser parser = new Parser();
        List<Lexer.Token> tokens = Arrays.asList(
                new Lexer.Token("player", "bet", 5),
                new Lexer.Token("player", "win", 5),
                new Lexer.Token("player", "lose", 5)
        );
        Parser.Program program = parser.parse(tokens);

        assertEquals(3, program.commands.size());
        assertEquals("bet", program.commands.get(0).action);
        assertEquals(5, program.commands.get(0).value);
        assertEquals("win", program.commands.get(1).action);
        assertEquals(5, program.commands.get(1).value);
        assertEquals("lose", program.commands.get(2).action);
        assertEquals(5, program.commands.get(2).value);
    }
}
