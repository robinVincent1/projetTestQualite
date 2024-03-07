package org.acme.models;

import java.util.List;

public class GameStateCardsChange {
    private Player player;
    private Dealer dealer;
    public GameStateCardsChange(Player player, Dealer dealer) {
        this.player = player;
        this.dealer = dealer;
    }

    public Player getPlayer() {
        return player;
    }

    public Dealer getDealer() {
        return dealer;
    }


    public void setDealer(Dealer dealer) {
        this.dealer = dealer;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }
}
