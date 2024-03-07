package org.acme.models;

import java.util.List;

public class Dealer {
    private int score;
    private boolean isWinner;
    private List<String> hand; // List de Card

    public Dealer(int score, boolean isWinner, List<String> hand) {
        this.score = score;
        this.isWinner = isWinner;
        this.hand = hand;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public boolean getIsWinner() {
        return isWinner;
    }

    public void setIsWinner(boolean isWinner) {
        this.isWinner = isWinner;
    }

    public List<String> getHand() {
        return hand;
    }

    public void setHand(List<String> hand) {
        this.hand = hand;
    }


}
