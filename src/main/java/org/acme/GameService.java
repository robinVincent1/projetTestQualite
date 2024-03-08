package org.acme;

//import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Event;
import jakarta.inject.Inject;
import org.acme.models.*;
import org.acme.models.gameEvent.GameEventMessage;



import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Arrays;


@ApplicationScoped
public class GameService {

    @Inject
    Event<GameEventMessage> gameEvent;

    private Player player;
    private Dealer dealer;
    private CardDeck cardDeck;

    private volatile boolean stopClock = false;

    private int timeclock = 10;

    public GameService() {
        // Créer un jeu de cartes
        List<String> cards = Arrays.asList("2-C.png", "2-D.png", "2-H.png", "2-S.png", "3-C.png", "3-D.png", "3-H.png", "3-S.png", "4-C.png", "4-D.png", "4-H.png", "4-S.png", "5-C.png", "5-D.png", "5-H.png", "5-S.png", "6-C.png", "6-D.png", "6-H.png", "6-S.png", "7-C.png", "7-D.png", "7-H.png", "7-S.png", "8-C.png", "8-D.png", "8-H.png", "8-S.png", "9-C.png", "9-D.png", "9-H.png", "9-S.png", "10-C.png", "10-D.png", "10-H.png", "10-S.png", "A-C.png", "A-D.png", "A-H.png", "A-S.png", "J-C.png", "J-D.png", "J-H.png", "J-S.png", "Q-C.png", "Q-D.png", "Q-H.png", "Q-S.png", "K-C.png", "K-D.png", "K-H.png", "K-S.png");
        cardDeck = new CardDeck("1", cards, 52);
    }


    public String getInitialState() {
        player = new Player("0", "Pseudo", new ArrayList<>(), 0,null, 100, 0, true, "Menu", false,timeclock,false);
        dealer = new Dealer(0, false, new ArrayList<>());
        return new GameEventMessage("INITIAL_STATE", new GameStateCardsChange(player, dealer)).toJson();
    }

    /*
    public void addPlayer(String id, String pseudo) {
        players.add(new Player(id, pseudo, new ArrayList<>(), 0, 100, 0, false, false,false));
    }

    /*public void removePlayer(String id) {
        players.removeIf(player -> player.getId().equals(id));
    }
    */



    private Thread clockThread;

    public void startClock() {
        stopClock = false;
        clockThread = new Thread(() -> {
            while (player.getClock() > 0 && !stopClock) {
                player.setClock(player.getClock() - 1);
                gameEvent.fire(new GameEventMessage("Clock", new GameStateCardsChange(player, dealer)));
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    throw new RuntimeException("Thread was interrupted", e);
                }
            }
            if (!stopClock) {
                hit(player.getId());
            }
        });
        clockThread.start();
    }

    public void stopClock() {
        stopClock = true;
        if (clockThread != null) {
            clockThread.interrupt();
        }
    }


    public void startGame() {
        // Distribuer les cartes aux joueurs

        distributeCardToPlayer(player);
        distributeCardToPlayer(player);
        // Distribuer les cartes au croupier
        distributeCardToDealer();
        distributeCardToDealer();
        startClock();
        // Envoyer l'état initial du jeu à tous les joueurs
        gameEvent.fire(new GameEventMessage("Deal", new GameStateCardsChange(player, dealer)));
    }

    /*
    private void distributeCardsToPlayers() {
        for (Player player : player) {
            for (int i = 0; i < 2; i++) {
                distributeCardToPlayer(player);
            }
        }
    }
    */

    private void distributeCardToPlayer(Player player) {
        String card = drawCard();
        player.getHand().add(card);
        int score = calculateCardScore(card);
        String[] cardSplit = card.split("-");
        if (cardSplit[0].equals("A")) {
            player.setAltScore(player.getScore() + 11);
            player.setScore(player.getScore() + 1);
        }
        else if(player.getAltScore() != null){
            player.setScore(player.getScore() + score);
            player.setAltScore(player.getAltScore() + score);
        }
        else {
            player.setScore(player.getScore() + score);
        }
    }



    private void distributeCardToDealer() {
        String card = drawCard();
        int score = calculateCardScore(card);
        dealer.getHand().add(card);
        dealer.setScore(dealer.getScore() + score);
    }

    private int calculateCardScore(String card) {
        String[] cardSplit = card.split("-");
        int score;
        if (cardSplit[0].equals("J") || cardSplit[0].equals("Q") || cardSplit[0].equals("K")) {
            score = 10;
        } else if (cardSplit[0].equals("A")) {
            score = 11;
        } else {
            try {
                score = Integer.parseInt(cardSplit[0]);
            } catch (NumberFormatException e) {
                // Gérer l'erreur de conversion
                score = 0;
            }
        }
        return score;
    }




    private String drawCard() {

        // Retirer une carte du jeu de cartes
        int index = (int) (Math.random() * cardDeck.getNbCards());
        String card = cardDeck.getCards().get(index); // Supprimer la carte à l'index spécifié
        cardDeck.setNbCards(cardDeck.getNbCards() - 1);
        return card;
    }

    public void insurance(String playerId){
        player.setWallet(player.getWallet() - player.getBet()/2);
        player.setBet(player.getBet() + player.getBet()/2);
        player.setAssurance(true);
        gameEvent.fire(new GameEventMessage("insurance", new GameStateCardsChange(player, dealer)));
    }


    public void hit(String playerId) {
        // Ajouter une carte à la main du joueur
        stopClock();

        String card = drawCard();
        //on ajoute le score de la carte au score du player
        String[] cardSplit = card.split("-");
        int score;
        Integer altscore = null;
        if (cardSplit[0].equals("J") || cardSplit[0].equals("Q") || cardSplit[0].equals("K")) {
            score = 10;
            altscore = 10;
        } else if (cardSplit[0].equals("A")) {
            score = 1;
            altscore = 11;

        } else {
            try {
                score = Integer.parseInt(cardSplit[0]);
            } catch (NumberFormatException e) {
                // Gérer l'erreur de conversion
                score = 0;
                altscore = 0 ;
            }
        };



        player.getHand().add(card);

        if(player.getAltScore()!=null){
            player.setAltScore(player.getAltScore() + altscore);
            player.setScore(player.getScore() + score);
        }
        else{
            player.setScore(player.getScore() + score);
        }

        if (player.getScore()==21){
            player.setGameStatus("BlackJack");
            stopClock();
        }
        if(player.getScore()>21){
            player.setGameStatus("Busted");
            stopClock();
        }
        else{
            player.setClock(timeclock);
            startClock();
        }
        gameEvent.fire(new GameEventMessage("hit",new GameStateCardsChange(player, dealer)));
    }

    public void doubleDown(String playerId){
        player.setWallet(player.getWallet() - player.getBet());
        player.setBet(player.getBet() * 2);
        String card = drawCard();
        int score = calculateCardScore(card);
        player.getHand().add(card);
        player.setScore(player.getScore() + score);
        System.out.println("doubleDown");
        stand(playerId);
    }


    public void stand(String playerId) {
        // Mettre fin au tour du joueur
        stopClock();
        player.setIsStanding(true);
        player.setIsPlaying(false);
        if (player.getAltScore() != null && player.getAltScore() <= 21) {
            player.setScore(player.getAltScore());
            player.setAltScore(null);
        }

        while (dealer.getScore() <= 17) {
            String card = drawCard();
            //on ajoute le score de la carte au score du dealer
            String[] cardSplit = card.split("-");
            int score;
            if (cardSplit[0].equals("J") || cardSplit[0].equals("Q") || cardSplit[0].equals("K")) {
                score = 10;
            } else if (cardSplit[0].equals("A")) {
                if(dealer.getScore() + 11 > 21){
                    score = 1;
                }
                else{
                    score = 11;
                }
            } else {
                try {
                    score = Integer.parseInt(cardSplit[0]);
                } catch (NumberFormatException e) {
                    // Gérer l'erreur de conversion
                    score = 0;
                }
            }
            dealer.getHand().add(card);
            dealer.setScore(dealer.getScore() + score);
        }
        if (dealer.getScore() == 21 && player.getAssurance() == true) {
            player.setGameStatus("InsuranceWinner");
            player.setWallet(player.getWallet() + 2 * player.getBet());
        }

        if (player.getScore() == dealer.getScore()) {
            player.setGameStatus("Tie");
            player.setWallet(player.getWallet() + player.getBet());
        }
        if (player.getScore() <= 21 && (player.getScore() > dealer.getScore() || dealer.getScore() > 21)) {
            player.setGameStatus("Winner");
            player.setWallet(player.getBet() * 2 + player.getWallet());
        }
        else{
            player.setGameStatus("Loser");
        }
        // Envoyer l'état du jeu mis à jour à tous les joueurs
        gameEvent.fire(new GameEventMessage("stand", new GameStateCardsChange(player, dealer)));
    }

    public void bet(String playerId, String amount) {
        // Mettre à jour le montant de la mise du joueur
        int amountInt = Integer.parseInt(amount);
        player.setBet(amountInt);
        player.setWallet(player.getWallet() - amountInt);
        player.setGameStatus("BetDone");
        // Envoyer l'état du jeu mis à jour à tous les joueurs
        gameEvent.fire(new GameEventMessage("bet", new GameStateCardsChange(player, dealer)));
    }

    public void reload(String playerId) {
        //on remet le isWInning a false
        player.setGameStatus("Menu");
        //on met le bet à 0
        player.setBet(0);
        //on met le deck à l'état initial
        player.setClock(timeclock);
        player.setIsStanding(false);
        player.setHand(new ArrayList<>());
        player.setScore(0);


        dealer.setHand(new ArrayList<>());
        dealer.setScore(0);
        List<String> cards = Arrays.asList("2-C.png", "2-D.png", "2-H.png", "2-S.png", "3-C.png", "3-D.png", "3-H.png", "3-S.png", "4-C.png", "4-D.png", "4-H.png", "4-S.png", "5-C.png", "5-D.png", "5-H.png", "5-S.png", "6-C.png", "6-D.png", "6-H.png", "6-S.png", "7-C.png", "7-D.png", "7-H.png", "7-S.png", "8-C.png", "8-D.png", "8-H.png", "8-S.png", "9-C.png", "9-D.png", "9-H.png", "9-S.png", "10-C.png", "10-D.png", "10-H.png", "10-S.png", "A-C.png", "A-D.png", "A-H.png", "A-S.png", "J-C.png", "J-D.png", "J-H.png", "J-S.png", "Q-C.png", "Q-D.png", "Q-H.png", "Q-S.png", "K-C.png", "K-D.png", "K-H.png", "K-S.png");
        cardDeck = new CardDeck("1", cards, 52);
        gameEvent.fire(new GameEventMessage("reload", new GameStateCardsChange(player, dealer)));

    }
}
