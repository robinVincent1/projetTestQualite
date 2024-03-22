package org.acme.compilateur;

import org.acme.models.Player;

public class PlayerController {

    public void playerPlay(Player player) {
        player.setWallet(player.getWallet() - 10);
        player.setBet(10);
        player.setWallet(player.getWallet() + 20);
        player.setWallet(player.getWallet() - 20);
        player.setBet(20);
        player.setWallet(player.getWallet() - 20);
}

}
