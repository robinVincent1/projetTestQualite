package org.acme.models;

import java.util.List;

public class Dealer {
    private int score; // score des cartes dans sa main
    private List<String> hand; // List des cartes

    //constructeur
    public Dealer(int score, List<String> hand) {
        this.score = score;
        this.hand = hand;
    }

    //get et set
    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public List<String> getHand() {
        return hand;
    }

    public void setHand(List<String> hand) {
        this.hand = hand;
    }


}
