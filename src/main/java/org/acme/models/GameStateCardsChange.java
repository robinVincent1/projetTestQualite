package org.acme.models;

import java.util.List;

// etat du jeu du joueur et du dealer
public class GameStateCardsChange {
    private Player player;
    private Dealer dealer;

    //constructeur
    public GameStateCardsChange(Player player, Dealer dealer) {
        this.player = player;
        this.dealer = dealer;
    }

    //get set
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
