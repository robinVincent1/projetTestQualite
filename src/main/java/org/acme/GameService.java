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

    private List<Player> players = new ArrayList<>();
    private Dealer dealer;
    private CardDeck cardDeck;

    public GameService() {
        // Créer un jeu de cartes
        List<String> cards = Arrays.asList("2-C.png", "2-D.png", "2-H.png", "2-S.png", "3-C.png", "3-D.png", "3-H.png", "3-S.png", "4-C.png", "4-D.png", "4-H.png", "4-S.png", "5-C.png", "5-D.png", "5-H.png", "5-S.png", "6-C.png", "6-D.png", "6-H.png", "6-S.png", "7-C.png", "7-D.png", "7-H.png", "7-S.png", "8-C.png", "8-D.png", "8-H.png", "8-S.png", "9-C.png", "9-D.png", "9-H.png", "9-S.png", "10-C.png", "10-D.png", "10-H.png", "10-S.png", "A-C.png", "A-D.png", "A-H.png", "A-S.png", "J-C.png", "J-D.png", "J-H.png", "J-S.png", "Q-C.png", "Q-D.png", "Q-H.png", "Q-S.png", "K-C.png", "K-D.png", "K-H.png", "K-S.png");
        cardDeck = new CardDeck("1", cards, 52);
    }


    public String getInitialState() {
        Player player = new Player(String.valueOf(players.size()), "hasard", new ArrayList<>(), 0, 100, 0, true, false, false);
        dealer = new Dealer(0, false, new ArrayList<>());
        players.add(player);
        return new GameEventMessage("INITIAL_STATE", new GameStateCardsChange(players, dealer, cardDeck)).toJson();
    }

    public void addPlayer(String id, String pseudo) {
        players.add(new Player(id, pseudo, new ArrayList<>(), 0, 100, 0, false, false,false));
    }

    public void removePlayer(String id) {
        players.removeIf(player -> player.getId().equals(id));
    }

    public void startGame() {
        // Distribuer les cartes aux joueurs
        distributeCardsToPlayers();
        // Distribuer les cartes au croupier
        distributeCardsToDealer();
        // Envoyer l'état initial du jeu à tous les joueurs
        gameEvent.fire(new GameEventMessage("Deal", new GameStateCardsChange(players, dealer, cardDeck)));
    }

    private void distributeCardsToPlayers() {
        for (Player player : players) {
            for (int i = 0; i < 2; i++) {
                distributeCardToPlayer(player);
            }
        }
    }

    private void distributeCardToPlayer(Player player) {
        String card = drawCard();
        int score = calculateCardScore(card);
        player.getHand().add(card);
        player.setScore(player.getScore() + score);
    }

    private void distributeCardsToDealer() {
        dealer = new Dealer(0, false, new ArrayList<>());
        for (int i = 0; i < 2; i++) {
            distributeCardToDealer();
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


    public void hit(String playerId) {
        // Ajouter une carte à la main du joueur

        Player player = players.stream().filter(p -> p.getId().equals(playerId)).findFirst().get();
        String card = drawCard();
        //on ajoute le score de la carte au score du player
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
        player.getHand().add(card);
        player.setScore(player.getScore() + score);

        // Envoyer l'état du jeu mis à jour à tous les joueurs
        gameEvent.fire(new GameEventMessage("hit",new GameStateCardsChange(players, dealer, cardDeck)));
    }


    public void stand(String playerId) {
        // Mettre fin au tour du joueur
        Player player = players.stream().filter(p -> p.getId().equals(playerId)).findFirst().get();
        player.setIsStanding(true);
        player.setIsPlaying(false);
        while (dealer.getScore() <= 17) {
            String card = drawCard();
            //on ajoute le score de la carte au score du dealer
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
            dealer.getHand().add(card);
            dealer.setScore(dealer.getScore() + score);
        }
        if (player.getScore() <= 21 && (player.getScore() > dealer.getScore() || dealer.getScore() > 21)) {
            player.setIsWinner(true);
            player.setWallet(player.getBet() * 2);
        }
        // Envoyer l'état du jeu mis à jour à tous les joueurs
        gameEvent.fire(new GameEventMessage("stand", new GameStateCardsChange(players, dealer, cardDeck)));
    }

    public void bet(String playerId, int amount) {
        // Mettre à jour le montant de la mise du joueur
        Player player = players.stream().filter(p -> p.getId().equals(playerId)).findFirst().get();
        player.setBet(amount);
        player.setWallet(player.getWallet() - amount);
        // Envoyer l'état du jeu mis à jour à tous les joueurs
        gameEvent.fire(new GameEventMessage("bet", new GameStateCardsChange(players, dealer, cardDeck)));
    }

    public void reload(String playerId) {
        Player player = players.stream().filter(p -> p.getId().equals(playerId)).findFirst().get();
        //on remet le isWInning a false
        player.setIsWinner(false);
        //on met le bet à 0
        player.setBet(0);
        //on met le deck à l'état initial
        List<String> cards = Arrays.asList("2-C.png", "2-D.png", "2-H.png", "2-S.png", "3-C.png", "3-D.png", "3-H.png", "3-S.png", "4-C.png", "4-D.png", "4-H.png", "4-S.png", "5-C.png", "5-D.png", "5-H.png", "5-S.png", "6-C.png", "6-D.png", "6-H.png", "6-S.png", "7-C.png", "7-D.png", "7-H.png", "7-S.png", "8-C.png", "8-D.png", "8-H.png", "8-S.png", "9-C.png", "9-D.png", "9-H.png", "9-S.png", "10-C.png", "10-D.png", "10-H.png", "10-S.png", "A-C.png", "A-D.png", "A-H.png", "A-S.png", "J-C.png", "J-D.png", "J-H.png", "J-S.png", "Q-C.png", "Q-D.png", "Q-H.png", "Q-S.png", "K-C.png", "K-D.png", "K-H.png", "K-S.png");
        cardDeck = new CardDeck("1", cards, 52);
        startGame();
    }
}
