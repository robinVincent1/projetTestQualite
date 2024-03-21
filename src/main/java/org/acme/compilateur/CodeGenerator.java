package org.acme.compilateur;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.List;

public class CodeGenerator {

    private final String className = "PlayerController";

    public void generateCode(Parser.Program program, String outputPath) throws IOException {
        StringBuilder classContent = new StringBuilder();

        classContent.append("package org.acme.compilateur;\n\n");
        classContent.append("import org.acme.models.Player;\n\n");
        classContent.append("public class ").append(className).append(" {\n\n");

        classContent.append("    public void playerPlay").append("(Player player) {\n");
        for (int i = 0; i < program.commands.size(); i++) {
            Parser.Command command = program.commands.get(i);

            switch (command.action) {
                case "win" ->
                        classContent.append("        player.setWallet(player.getWallet() + ").append(2 * command.value).append(");\n");
                case "lose" -> {
                    classContent.append("        player.setWallet(player.getWallet() - ").append(command.value).append(");\n");
                }
                case "bet" -> {
                    classContent.append("        player.setWallet(player.getWallet() - ").append(command.value).append(");\n");
                    classContent.append("        player.setBet(").append(command.value).append(");\n");
                }
            }
            classContent.append("    }\n\n");
        }
        classContent.append("}\n\n");
        // Voici la partie ajoutée pour écrire le contenu dans un fichier
        Files.writeString(Paths.get(outputPath), classContent.toString(), StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
    }
}