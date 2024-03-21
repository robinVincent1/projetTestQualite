package org.acme.game.compilateur;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.acme.compilateur.Lexer;
import org.junit.jupiter.api.Test;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class LexerTest {

    @Test
    public void testTokenizeSimpleCommands() throws Exception {
        Path tempFile = Files.createTempFile(null, ".txt");
        Files.write(tempFile, List.of("player bet 5", "player win 5"));

        Lexer lexer = new Lexer();
        List<Lexer.Token> tokens = lexer.tokenize(tempFile.toString());

        assertEquals(2, tokens.size());
        assertEquals("bet", tokens.get(0).decision);
        assertEquals(5, tokens.get(0).amount);
        assertEquals("win", tokens.get(1).decision);

        Files.deleteIfExists(tempFile); // Nettoyer le fichier temporaire
    }
}
