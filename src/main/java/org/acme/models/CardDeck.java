package org.acme.models;

import java.util.List;

public class CardDeck {
    private String id;
    private List<String> cards; // List des cartes
    private int nbCards; // nombre de carte dans le jeu

    //constructeur
    public CardDeck(String id, List<String> cards, int nbCards) {
        this.id = id;
        this.cards = cards;
        this.nbCards = nbCards;
    }

    //get et set
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<String> getCards() {
        return cards;
    }

    public void setCards(List<String> cards) {
        this.cards = cards;
    }

    public int getNbCards() {
        return nbCards;
    }

    public void setNbCards(int nbCards) {
        this.nbCards = nbCards;
    }

    // fonction pour enlever une carte du packet
    public void removeCard(String card) {
        cards.remove(card);
        nbCards--;
    }

}
