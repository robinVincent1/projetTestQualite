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
    Event<GameEventMessage> gameEvent; // Injection d'un événement pour envoyer des messages de jeu

    // Déclaration des variables de jeu
    private Player player; // Représente le joueur
    private Dealer dealer; // Représente le croupier
    private CardDeck cardDeck; // Représente le jeu de cartes

    private volatile boolean stopClock = false; // Indique si l'horloge du jeu doit être arrêtée
    private int timeclock = 10; // Temps alloué pour chaque tour du joueur

    // Constructeur
    public GameService() {
        // Crée un jeu de cartes avec un ensemble prédéfini de cartes
        List<String> cards = Arrays.asList("2-C.png", "2-D.png", "2-H.png", "2-S.png", "3-C.png", "3-D.png", "3-H.png", "3-S.png", "4-C.png", "4-D.png", "4-H.png", "4-S.png", "5-C.png", "5-D.png", "5-H.png", "5-S.png", "6-C.png", "6-D.png", "6-H.png", "6-S.png", "7-C.png", "7-D.png", "7-H.png", "7-S.png", "8-C.png", "8-D.png", "8-H.png", "8-S.png", "9-C.png", "9-D.png", "9-H.png", "9-S.png", "10-C.png", "10-D.png", "10-H.png", "10-S.png", "A-C.png", "A-D.png", "A-H.png", "A-S.png", "J-C.png", "J-D.png", "J-H.png", "J-S.png", "Q-C.png", "Q-D.png", "Q-H.png", "Q-S.png", "K-C.png", "K-D.png", "K-H.png", "K-S.png");; // Liste des noms de cartes
        cardDeck = new CardDeck("1", cards, 52); // Crée un jeu de cartes avec un ID, une liste de cartes et le nombre total de cartes
    }

    // Méthodes d'accès aux variables du jeu
    public void setStopClock(boolean stopClock) {
        this.stopClock = stopClock; // Modifie l'état de l'horloge du jeu
    }

    public boolean getStopClock() {
        return stopClock; // Renvoie l'état de l'horloge du jeu
    }

    public Dealer getDealer() {
        return dealer; // Renvoie le croupier actuel du jeu
    }

    public Player getPlayer() {
        return player; // Renvoie le joueur actuel du jeu
    }

    public int getTimeclock() {
        return timeclock; // Renvoie le temps alloué pour chaque tour du joueur
    }

    // Méthodes de configuration des événements de jeu et du jeu de cartes
    public void setGameEvent(Event<GameEventMessage> gameEvent) {
        this.gameEvent = gameEvent; // Configure l'événement de jeu pour envoyer des messages de jeu
    }

    public void setCardDeck(CardDeck cardDeck) {
        this.cardDeck = cardDeck; // Configure le jeu de cartes pour le jeu
    }

    // Méthodes de configuration du croupier et du joueur
    public void setDealer(Dealer dealer) {
        this.dealer = dealer; // Configure le croupier actuel du jeu
    }

    public void setPlayer(Player player) {
        this.player = player; // Configure le joueur actuel du jeu
    }

    // Méthodes pour démarrer et arrêter l'horloge du jeu
    private Thread clockThread; // Thread pour gérer l'horloge du jeu

    public String getInitialState() {
        // Créer un nouveau joueur avec des valeurs par défaut
        player = new Player("0", "Pseudo", new ArrayList<>(), 0, null, 100, 0, true, "Menu", timeclock, false);
        // Créer un nouveau croupier avec un score initial de 0 et une main vide
        dealer = new Dealer(0, new ArrayList<>());
        // Créer un objet GameEventMessage pour représenter l'état initial du jeu
        GameEventMessage initialStateMessage = new GameEventMessage("INITIAL_STATE", new GameStateCardsChange(player, dealer));
        // Convertir l'objet GameEventMessage en format JSON
        return initialStateMessage.toJson();
    }


    public void startClock() {
        stopClock = false; // Réinitialiser le drapeau pour arrêter l'horloge
        clockThread = new Thread(() -> {
            // Boucle pour décompter le temps restant du joueur
            while (player.getClock() > 0 && !stopClock) {
                // Réduire d'une seconde le temps restant du joueur
                player.setClock(player.getClock() - 1);
                // Envoyer un événement de mise à jour de l'horloge à tous les joueurs
                gameEvent.fire(new GameEventMessage("Clock", new GameStateCardsChange(player, dealer)));
                try {
                    Thread.sleep(1000); // Attendre une seconde avant de réduire à nouveau le temps restant
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    throw new RuntimeException("Thread was interrupted", e);
                }
            }
            // Si l'horloge n'est pas arrêtée, effectuer un tirage automatique pour le joueur
            if (!stopClock) {
                hit(player.getId());
            }
        });
        clockThread.start(); // Démarrer le thread de l'horloge
    }


    public void stopClock() {
        stopClock = true; // Arrêter l'horloge
        if (clockThread != null) {
            clockThread.interrupt(); // Interrompre le thread de l'horloge
        }
    }



    public void startGame() {
        // Distribuer les cartes aux joueurs
        distributeCardToPlayer(player);
        distributeCardToPlayer(player);
        // Distribuer les cartes au croupier
        distributeCardToDealer();
        distributeCardToDealer();
        startClock(); // Démarrer l'horloge du joueur
        // Envoyer l'état initial du jeu à tous les joueurs
        gameEvent.fire(new GameEventMessage("Deal", new GameStateCardsChange(player, dealer)));
    }


    // Définit le pseudo du joueur avec la valeur spécifiée
    public void pseudo(String playerId, String pseudo) {
        player.setPseudo(pseudo);
        // Envoie un événement de jeu pour la mise à jour du pseudo à tous les joueurs
        gameEvent.fire(new GameEventMessage("pseudo", new GameStateCardsChange(player, dealer)));
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

    // Tire une carte du jeu de cartes
    private void distributeCardToPlayer(Player player) {
        String card = drawCard();
        // Ajoute la carte à la main du joueur
        player.getHand().add(card);
        // Calcule le score de la carte
        int score = calculateCardScore(card);
        String[] cardSplit = card.split("-");
        // Si la carte est un As, ajuste le score du joueur en conséquence
        if (cardSplit[0].equals("A")) {
            player.setAltScore(player.getScore() + 11); // Met à jour l'alternative de score avec 11
            player.setScore(player.getScore() + 1); // Met à jour le score avec 1
        }
        // Si le joueur a déjà une alternative de score, met à jour les scores
        else if(player.getAltScore() != null){
            player.setScore(player.getScore() + score); // Met à jour le score
            player.setAltScore(player.getAltScore() + score); // Met à jour l'alternative de score
        }
        // Sinon, met à jour simplement le score du joueur
        else {
            player.setScore(player.getScore() + score);
        }
    }


    // Tire une carte du jeu de cartes
    private void distributeCardToDealer() {
        String card = drawCard();
        // Calcule le score de la carte
        int score = calculateCardScore(card);
        // Ajoute la carte à la main du dealer
        dealer.getHand().add(card);
        // Met à jour le score du dealer en ajoutant le score de la carte
        dealer.setScore(dealer.getScore() + score);
    }


    // Divise la carte pour récupérer sa valeur et sa couleur
    private int calculateCardScore(String card) {
        String[] cardSplit = card.split("-");
        int score;
        // Détermine le score en fonction de la valeur de la carte
        if (cardSplit[0].equals("J") || cardSplit[0].equals("Q") || cardSplit[0].equals("K")) {
            score = 10;
        } else if (cardSplit[0].equals("A")) {
            score = 11;
        } else {
            try {
                score = Integer.parseInt(cardSplit[0]);
            } catch (NumberFormatException e) {
                // Gère l'erreur de conversion
                score = 0;
            }
        }
        return score; // Retourne le score calculé
    }


    // Retirer une carte du jeu de cartes
    private String drawCard() {
        int index = (int) (Math.random() * cardDeck.getNbCards()); // Générer un index aléatoire pour choisir une carte
        String card = cardDeck.getCards().get(index); // Récupérer la carte à l'index spécifié
        cardDeck.getCards().remove(index); // Supprimer la carte de la liste des cartes du jeu de cartes
        cardDeck.setNbCards(cardDeck.getNbCards() - 1); // Mettre à jour le nombre de cartes restantes dans le jeu de cartes
        return card; // Retourner la carte tirée
    }


    public void insurance(String playerId){
        player.setWallet(player.getWallet() - player.getBet()/2);
        player.setBet(player.getBet() + player.getBet()/2);
        player.setAssurance(true);
        gameEvent.fire(new GameEventMessage("insurance", new GameStateCardsChange(player, dealer)));
    }

    // Ajouter une carte à la main du joueur
    public void hit(String playerId) {
        stopClock();

        // Tirer une carte du jeu
        String card = drawCard();

        // Séparer la carte en valeur et en enseigne
        String[] cardSplit = card.split("-");

        // Initialiser les scores pour la carte tirée
        int score;
        Integer altscore;

        // Déterminer le score de la carte
        if (cardSplit[0].equals("J") || cardSplit[0].equals("Q") || cardSplit[0].equals("K")) {
            // Pour les figures (J, Q, K), le score est de 10
            score = 10;
            altscore = 10;
        } else if (cardSplit[0].equals("A")) {
            // Pour l'As, le score peut être 1 ou 11
            score = 1;
            altscore = 11;
        } else {
            try {
                // Pour les autres cartes, le score est leur valeur numérique
                score = Integer.parseInt(cardSplit[0]);
                altscore = score;
            } catch (NumberFormatException e) {
                // En cas d'erreur de conversion, assigner un score de 0
                score = 0;
                altscore = 0;
            }
        }

        // Ajouter la carte à la main du joueur
        player.getHand().add(card);

        // Mettre à jour le score du joueur en fonction de la carte tirée
        if (player.getAltScore() != null) {
            // Si le joueur a un score alternatif
            player.setAltScore(player.getAltScore() + altscore);
            player.setScore(player.getScore() + score);
            // Vérifier si le score alternatif dépasse 21
            if (player.getAltScore() > 21) {
                player.setAltScore(null);
            }
        } else if (player.getAltScore() == null && cardSplit[0].equals("A")) {
            // Si le joueur n'a pas de score alternatif et tire un As
            player.setAltScore(player.getScore() + altscore);
            player.setScore(player.getScore() + score);
            // Vérifier si le score alternatif dépasse 21
            if (player.getAltScore() > 21) {
                player.setAltScore(null);
            }
        } else {
            // Si le joueur n'a pas de score alternatif et tire une autre carte
            player.setScore(player.getScore() + score);
        }

        // Traiter les résultats du tirage
        if (player.getScore() == 21) {
            // Si le joueur atteint 21, marquer comme BlackJack
            player.setGameStatus("BlackJack");
            stopClock();
        } else if (player.getScore() > 21) {
            // Si le joueur dépasse 21, marquer comme Busted
            player.setGameStatus("Busted");
            setStopClock(false);
            stopClock();
        } else {
            // Si le jeu continue, redémarrer l'horloge
            player.setClock(timeclock);
            startClock();
        }

        // Envoyer un événement de jeu contenant les nouvelles cartes à tous les joueurs
        gameEvent.fire(new GameEventMessage("hit", new GameStateCardsChange(player, dealer)));
    }


    //méthode pour doubler la mise du player
    public void doubleDown(String playerId){
        // Soustraire la mise du joueur de son portefeuille
        player.setWallet(player.getWallet() - player.getBet());
        // Doubler la mise du joueur
        player.setBet(player.getBet() * 2);
        // Tirer une carte supplémentaire pour le joueur
        String card = drawCard();
        // Calculer le score de la carte tirée
        int score = calculateCardScore(card);
        // Ajouter la carte à la main du joueur
        player.getHand().add(card);
        // Mettre à jour le score du joueur
        player.setScore(player.getScore() + score);
        // Afficher un message de confirmation
        System.out.println("doubleDown");
        // Mettre fin au tour du joueur en se tenant debout
        stand(playerId);
    }


    //méthode appelée quand le joueur souhaite terminer une partie
    public void stand(String playerId) {
        // Mettre fin au tour du joueur en arrêtant l'horloge
        stopClock();
        // Marquer le joueur comme étant debout
        player.setIsStanding(true);
        // Mettre à jour le score du joueur s'il y a un score alternatif
        if (player.getAltScore() != null && player.getAltScore() <= 21) {
            player.setScore(player.getAltScore());
            player.setAltScore(null);
        }

        // Tirer des cartes pour le croupier tant que son score est inférieur ou égal à 17
        while (dealer.getScore() <= 17) {
            // Tirer une carte pour le croupier
            String card = drawCard();
            // Calculer le score de la carte tirée pour le croupier
            int score = calculateCardScore(card);
            // Ajouter la carte à la main du croupier
            dealer.getHand().add(card);
            // Mettre à jour le score du croupier
            dealer.setScore(dealer.getScore() + score);
        }

        // Traiter les résultats du jeu entre le joueur et le croupier
        // si le croupier a blackJack et que le joueur a pris l'assurance
        if (dealer.getScore() == 21 && player.getAssurance()) {
            player.setGameStatus("InsuranceWinner");
            player.setWallet(player.getWallet() + 2 * player.getBet());
        } else if (player.getScore() == dealer.getScore()) {
            player.setGameStatus("Tie"); // si les score du dealer et du player sont les memes : égalité
            player.setWallet(player.getWallet() + player.getBet());
        } else if (player.getScore() <= 21 && (player.getScore() > dealer.getScore() || dealer.getScore() > 21)) {
            player.setGameStatus("Winner"); // le player gagne
            player.setWallet(player.getBet() * 2 + player.getWallet());
        } else {
            player.setGameStatus("Loser"); // le player perds
        }
        // Envoyer l'état du jeu mis à jour à tous les joueurs
        gameEvent.fire(new GameEventMessage("stand", new GameStateCardsChange(player, dealer)));
    }


    //méthode pour miser
    public void bet(String playerId, String amount) {
        // Mettre à jour le montant de la mise du joueur
        int amountInt = Integer.parseInt(amount); // Convertir la chaîne de caractères en entier
        player.setBet(amountInt); // Mettre à jour la mise du joueur
        player.setWallet(player.getWallet() - amountInt); // Soustraire le montant de la mise au portefeuille du joueur
        player.setGameStatus("BetDone"); // Mettre à jour le statut du jeu du joueur
        // Envoyer l'état du jeu mis à jour à tous les joueurs
        gameEvent.fire(new GameEventMessage("bet", new GameStateCardsChange(player, dealer)));
    }

//méthode pour relancer une partie
    public void reload(String playerId) {
        // Remettre le statut du jeu à "Menu"
        player.setGameStatus("Menu");
        // Réinitialiser la mise du joueur à 0
        player.setBet(0);
        // Réinitialiser le deck de cartes à son état initial
        player.setAltScore(null); // Réinitialiser le score alternatif du joueur
        player.setClock(timeclock); // Réinitialiser l'horloge du joueur
        player.setIsStanding(false); // Réinitialiser le statut de "debout" du joueur
        player.setHand(new ArrayList<>()); // Réinitialiser la main du joueur
        player.setScore(0); // Réinitialiser le score du joueur

        dealer.setHand(new ArrayList<>()); // Réinitialiser la main du croupier
        dealer.setScore(0); // Réinitialiser le score du croupier

        // Créer un nouveau deck de cartes et l'affecter au service de jeu
        List<String> cards =  Arrays.asList("2-C.png", "2-D.png", "2-H.png", "2-S.png", "3-C.png", "3-D.png", "3-H.png", "3-S.png", "4-C.png", "4-D.png", "4-H.png", "4-S.png", "5-C.png", "5-D.png", "5-H.png", "5-S.png", "6-C.png", "6-D.png", "6-H.png", "6-S.png", "7-C.png", "7-D.png", "7-H.png", "7-S.png", "8-C.png", "8-D.png", "8-H.png", "8-S.png", "9-C.png", "9-D.png", "9-H.png", "9-S.png", "10-C.png", "10-D.png", "10-H.png", "10-S.png", "A-C.png", "A-D.png", "A-H.png", "A-S.png", "J-C.png", "J-D.png", "J-H.png", "J-S.png", "Q-C.png", "Q-D.png", "Q-H.png", "Q-S.png", "K-C.png", "K-D.png", "K-H.png", "K-S.png");; // Liste des noms de cartes
        cardDeck = new CardDeck("1", cards, 52); // Création d'un nouveau deck de cartes
        // Envoyer un événement de jeu contenant les informations de jeu réinitialisées à tous les joueurs
        gameEvent.fire(new GameEventMessage("reload", new GameStateCardsChange(player, dealer)));
    }

}
