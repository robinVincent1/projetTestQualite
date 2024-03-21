package org.acme.game.compilateur;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.acme.compilateur.CodeGenerator;
import org.acme.compilateur.Parser;
import org.junit.jupiter.api.Test;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class CodeGeneratorTest {

    @Test
    public void testGenerateCode() throws Exception {
        // Création directe d'un programme avec des commandes
        List<Parser.Command> commands = List.of(new Parser.Command("bet", 5));
        Parser.Program program = new Parser.Program(commands);

        CodeGenerator codeGenerator = new CodeGenerator();
        String outputPath = "src/test/java/org.acme/game/compilateur/PlayerController.java";

        // Génération du code
        codeGenerator.generateCode(program, outputPath);

        // Lecture et vérification du contenu généré
        String generatedCode = Files.readString(Paths.get(outputPath));
        assertTrue(generatedCode.contains("public class PlayerController"), "Le code généré doit contenir 'public class PlayerController'");
        assertTrue(generatedCode.contains("        player.setWallet(player.getWallet() - "), "Le code généré doit retirer 5€ du portefeuille du joueur");

    }
}
