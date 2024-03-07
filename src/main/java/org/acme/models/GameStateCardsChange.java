package org.acme.models;

import java.util.List;

public class GameStateCardsChange {
    private List<Player> players;
    private Dealer dealer;
    private CardDeck cardDeck;
    public GameStateCardsChange( List<Player> players, Dealer dealer, CardDeck cardDeck) {
        this.players = players;
        this.dealer = dealer;
        this.cardDeck = cardDeck;
    }

    public List<Player> getPlayer() {
        return players;
    }

    public Dealer getDealer() {
        return dealer;
    }

    public CardDeck getCardDeck() {
        return cardDeck;
    }

    public void setCardDeck(CardDeck cardDeck) {
        this.cardDeck = cardDeck;
    }

    public void setDealer(Dealer dealer) {
        this.dealer = dealer;
    }

    public void setPlayer(List<Player> player) {
        this.players = player;
    }
}
