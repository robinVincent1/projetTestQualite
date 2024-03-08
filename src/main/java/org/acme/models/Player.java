package org.acme.models;

import java.util.List;



public class Player {
    private String id;
    private String pseudo;

    private List<String> hand; // List des cartes dans sa main

    private int score; // score des cartes dans sa main

    private Integer altscore; // score alternatif en cas d'as dans la main qui vaut 1 ou 11
    private double wallet; //porte-feuille
    private double bet; //mise sur la partie

    private int clock; //timer

    private boolean assurance; // mise sur le blackjack du dealer

    private String GameStatus; // status de la partie
    private boolean isStanding; // partie fini ou non

    //constructeur

    public Player(String id, String pseudo, List<String> hand, int score,Integer altscore, double wallet, double bet, boolean isStanding , String GameStatus, int clock, boolean assurance) {
        this.id = id;
        this.pseudo = pseudo;
        this.hand = hand;
        this.score = score;
        this.altscore = altscore;
        this.wallet = wallet;
        this.bet = bet;
        this.GameStatus = GameStatus;
        this.clock = clock;
        this.assurance = assurance;
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

    public double getWallet() {
        return wallet;
    }

    public void setWallet(double wallet) {
        this.wallet = wallet;
    }

    public double getBet() {
        return bet;
    }

    public void setBet(double bet) {
        this.bet = bet;
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

    public boolean getAssurance() {
        return assurance;
    }

    public void setAssurance(boolean assurance) {
        this.assurance = assurance;
    }

}
