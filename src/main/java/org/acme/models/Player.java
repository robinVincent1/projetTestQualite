package org.acme.models;

import java.util.List;



public class Player {
    private String id;
    private String pseudo;

    private List<String> hand; // List de Card

    private int score;

    private Integer altscore;
    private int wallet;
    private int bet;

    private int clock;

    private boolean isStanding;
    private boolean isPlaying;
    private String GameStatus;

    public Player(String id, String pseudo, List<String> hand, int score,Integer altscore, int wallet, int bet, boolean isPlaying, String GameStatus , boolean isStanding, int clock) {
        this.id = id;
        this.isStanding = isStanding;
        this.pseudo = pseudo;
        this.hand = hand;
        this.score = score;
        this.altscore = altscore;
        this.wallet = wallet;
        this.bet = bet;
        this.isPlaying = isPlaying;
        this.GameStatus = GameStatus;
        this.clock = clock;
    }

    public boolean getIsStanding() {
        return isStanding;
    }

    public void setIsStanding(boolean isStanding) {
        this.isStanding = isStanding;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPseudo() {
        return pseudo;
    }

    public void setPseudo(String pseudo) {
        this.pseudo = pseudo;
    }

    public List<String> getHand() {
        return hand;
    }

    public void setHand(List<String> hand) {
        this.hand = hand;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public int getWallet() {
        return wallet;
    }

    public void setWallet(int wallet) {
        this.wallet = wallet;
    }

    public int getBet() {
        return bet;
    }

    public void setBet(int bet) {
        this.bet = bet;
    }

    public boolean getIsPlaying() {
        return isPlaying;
    }

    public void setIsPlaying(boolean isPlaying) {
        this.isPlaying = isPlaying;
    }

    public String getGameStatus() {
        return GameStatus;
    }

    public void setGameStatus(String GameStatus) {
        this.GameStatus = GameStatus;
    }

    public int getClock() {
        return clock;
    }

    public void setClock(int clock) {
        this.clock = clock;
    }

    public Integer getAltScore() {
        return altscore;
    }

    public void setAltScore(Integer altscore) {
        this.altscore = altscore;
    }



}
