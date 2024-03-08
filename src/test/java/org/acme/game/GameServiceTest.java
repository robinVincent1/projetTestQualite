package org.acme.game;

import org.acme.GameService;
import org.acme.models.CardDeck;
import org.acme.models.Dealer;
import org.acme.models.GameStateCardsChange;
import org.acme.models.Player;
import org.acme.models.gameEvent.GameEventMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import jakarta.enterprise.event.Event;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class GameServiceTest {

    private GameService gameService; // Référence vers le service de jeu à tester

    @Mock
    private Event<GameEventMessage> gameEvent; // Simulation de l'événement de jeu

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this); // Initialiser les champs annotés par Mock
        gameService = new GameService(); // Initialiser le service de jeu
        gameService.setGameEvent(gameEvent); // Injecter l'instance simulée de Event<GameEventMessage>
    }

    @Test
    public void testGetInitialState() {
        // Teste la méthode getInitialState() du service de jeu
        String initialState = gameService.getInitialState(); // Obtient l'état initial du jeu
        assertNotNull(initialState); // Vérifie que l'état initial n'est pas nul

        // Crée un joueur et un croupier avec des attributs spécifiques pour comparer avec l'état initial attendu
        Player player = new Player("0", "Pseudo", new ArrayList<>(), 0,null, 100, 0, true, "Menu",10,false);
        Dealer dealer = new Dealer(0, new ArrayList<>());
        // Crée une chaîne JSON représentant l'état initial attendu du jeu
        String expectedInitialState = new GameEventMessage("INITIAL_STATE", new GameStateCardsChange(player, dealer)).toJson();

        // Vérifie si l'état initial est une chaîne JSON valide ou contient les bonnes informations
        assertEquals(expectedInitialState, initialState);
    }


    @Test
    public void testStartGame() {
        gameService.getInitialState();
        gameService.startGame();
        Player player = gameService.getPlayer();
        Dealer dealer = gameService.getDealer();
        // Vérifier si le joueur a reçu deux cartes
        assertEquals(2, gameService.getPlayer().getHand().size());
        // Vérifier si le croupier a reçu deux cartes
        assertEquals(2, gameService.getDealer().getHand().size());
        // Vérifier si le score du joueur est correctement calculé
        assertTrue(player.getScore() >= 4 && player.getScore() <= 21);
    }

    @Test
    public void testHit() {
        gameService.getInitialState();
        // Test pour une carte 10, 9 et un As
        List<String> cards = Arrays.asList("10-C.png", "9-D.png", "A-H.png");
        gameService.setCardDeck(new CardDeck("1", cards, cards.size()));
        for (String card : cards) {
            gameService.hit("playerId");

            // Vérifier si le joueur a un Blackjack
            if (gameService.getPlayer().getScore() == 21 && gameService.getPlayer().getHand().size() == 2) {
                assertEquals("BlackJack", gameService.getPlayer().getGameStatus());
                assertTrue(gameService.getPlayer().getIsStanding());
            }

            // Vérifier si le joueur a dépassé 21
            if (gameService.getPlayer().getScore() > 21) {
                assertEquals("Busted", gameService.getPlayer().getGameStatus());
            }

            // Vérifier si le jeu continue
            if (gameService.getPlayer().getScore() < 21) {
            }
        }
    }
    @Test
    public void testStandDealerBlackJack() {
        // Initialise l'état du jeu
        gameService.getInitialState();
        // Le croupier obtient un blackjack
        gameService.getPlayer().setScore(20); // Le joueur a un score de 20
        gameService.getDealer().setScore(21); // Le croupier a un blackjack
        gameService.stand("playerId"); // Le joueur choisit de rester
        // Vérifie que le joueur est déclaré perdant
        assertEquals("Loser", gameService.getPlayer().getGameStatus());
        // Vérifie que l'événement de jeu a été déclenché une fois
        verify(gameEvent, times(1)).fire(any(GameEventMessage.class));
    }

    @Test
    public void testStandPlayerTie() {
        // Initialise l'état du jeu
        gameService.getInitialState();
        // Le joueur obtient un score égal à celui du croupier
        gameService.getPlayer().setScore(18); // Le joueur a un score de 18
        gameService.getDealer().setScore(18); // Le croupier a un score de 18
        gameService.getPlayer().setBet(100); // Le joueur mise 100 jetons
        gameService.getPlayer().setWallet(0); // Le portefeuille du joueur est vide
        gameService.stand("playerId"); // Le joueur choisit de rester
        // Vérifie que le joueur et le croupier font match nul
        assertEquals("Tie", gameService.getPlayer().getGameStatus());
        // Vérifie que le portefeuille du joueur n'est pas affecté
        assertEquals(100, gameService.getPlayer().getWallet());
        // Vérifie que l'événement de jeu a été déclenché une fois
        verify(gameEvent, times(1)).fire(any(GameEventMessage.class));
    }

    @Test
    public void testStandPlayerWins() {
        // Initialise l'état du jeu
        gameService.getInitialState();
        // Le joueur gagne
        gameService.getPlayer().setScore(19); // Le joueur a un score de 19
        gameService.getDealer().setScore(17); // Le croupier a un score de 17
        gameService.getPlayer().setBet(100); // Le joueur mise 100 jetons
        gameService.getPlayer().setWallet(0); // Le portefeuille du joueur est vide
        gameService.stand("playerId"); // Le joueur choisit de rester
        // Vérifie que le joueur est déclaré vainqueur
        assertEquals("Winner", gameService.getPlayer().getGameStatus());
        // Vérifie que le portefeuille du joueur est crédité du double du montant de sa mise
        assertEquals(200, gameService.getPlayer().getWallet());
        // Vérifie que l'événement de jeu a été déclenché une fois
        verify(gameEvent, times(1)).fire(any(GameEventMessage.class));
    }

    @Test
    public void testStandPlayerLoses() {
        // Initialise l'état du jeu
        gameService.getInitialState();
        // Le joueur perd
        gameService.getPlayer().setScore(15); // Le joueur a un score de 15
        gameService.getDealer().setScore(20); // Le croupier a un score de 20
        gameService.getPlayer().setBet(100); // Le joueur mise 100 jetons
        gameService.getPlayer().setWallet(0); // Le portefeuille du joueur est vide
        gameService.stand("playerId"); // Le joueur choisit de rester
        // Vérifie que le joueur est déclaré perdant
        assertEquals("Loser", gameService.getPlayer().getGameStatus());
        // Vérifie que le portefeuille du joueur est vide
        assertEquals(0, gameService.getPlayer().getWallet());
        // Vérifie que l'événement de jeu a été déclenché une fois
        verify(gameEvent, times(1)).fire(any(GameEventMessage.class));
    }

    @Test
    public void testStandDealerBusted() {
        // Initialise l'état du jeu
        gameService.getInitialState();
        // Le croupier dépasse 21
        gameService.getPlayer().setScore(18); // Le joueur a un score de 18
        gameService.getDealer().setScore(22); // Le croupier dépasse 21 (bust)
        gameService.getPlayer().setBet(100); // Le joueur mise 100 jetons
        gameService.getPlayer().setWallet(0); // Le portefeuille du joueur est vide
        gameService.stand("playerId"); // Le joueur choisit de rester
        // Vérifie que le joueur est déclaré vainqueur
        assertEquals("Winner", gameService.getPlayer().getGameStatus());
        // Vérifie que le portefeuille du joueur est crédité du double du montant de sa mise
        assertEquals(200, gameService.getPlayer().getWallet());
        // Vérifie que l'événement de jeu a été déclenché une fois
        verify(gameEvent, times(1)).fire(any(GameEventMessage.class));
    }


    @Test
    public void testStandInsuranceWinner() {
        // Initialise l'état du jeu
        gameService.getInitialState();
        // Le croupier obtient un blackjack et le joueur a pris une assurance
        gameService.getPlayer().setAssurance(true); // Le joueur a pris une assurance
        gameService.getPlayer().setScore(20); // Le score du joueur est de 20
        gameService.getDealer().setScore(21); // Le score du croupier est de 21 (blackjack)
        gameService.getPlayer().setBet(100); // Le joueur mise 100 jetons
        gameService.getPlayer().setWallet(0); // Le portefeuille du joueur est vide
        // Le joueur choisit de rester
        gameService.stand("playerId");
        // Vérifie que le joueur est déclaré vainqueur de l'assurance
        assertEquals("InsuranceWinner", gameService.getPlayer().getGameStatus());
        // Vérifie que le portefeuille du joueur est crédité du double du montant de sa mise
        assertEquals(200, gameService.getPlayer().getWallet());
        // Vérifie que l'événement de jeu a été déclenché une fois
        verify(gameEvent, times(1)).fire(any(GameEventMessage.class));
    }

    @Test
    public void testBet() {
        // Initialise l'état du jeu
        gameService.getInitialState();
        // Le joueur place une mise de 50 jetons
        gameService.bet("playerId", "50");
        // Vérifie que la mise du joueur est correcte
        assertEquals(50, gameService.getPlayer().getBet());
        // Vérifie que le portefeuille du joueur est correct
        assertEquals(50, gameService.getPlayer().getWallet());
        // Vérifie que le statut de jeu du joueur est mis à "BetDone"
        assertEquals("BetDone", gameService.getPlayer().getGameStatus());
        // Vérifie que l'événement de jeu a été déclenché une fois
        verify(gameEvent, times(1)).fire(any(GameEventMessage.class));
    }

    @Test
    public void testReload() {
        // Initialise l'état du jeu
        gameService.getInitialState();
        // Le joueur recharge son portefeuille et réinitialise le jeu
        gameService.reload("playerId");

        // Vérifie que le statut de jeu du joueur est "Menu"
        assertEquals("Menu", gameService.getPlayer().getGameStatus());
        // Vérifie que la mise du joueur est réinitialisée à 0
        assertEquals(0, gameService.getPlayer().getBet());
        // Vérifie que le score alternatif du joueur est nul
        assertNull(gameService.getPlayer().getAltScore());
        // Vérifie que le décompte de temps du joueur est de 10 secondes
        assertEquals(10, gameService.getPlayer().getClock());
        // Vérifie que le joueur n'est pas en position de rester
        assertFalse(gameService.getPlayer().getIsStanding());
        // Vérifie que la main du joueur est vide
        assertTrue(gameService.getPlayer().getHand().isEmpty());
        // Vérifie que le score du joueur est réinitialisé à 0
        assertEquals(0, gameService.getPlayer().getScore());

        // Vérifie que la main du croupier est vide
        assertTrue(gameService.getDealer().getHand().isEmpty());
        // Vérifie que le score du croupier est réinitialisé à 0
        assertEquals(0, gameService.getDealer().getScore());

        // Vérifie que l'événement de jeu a été déclenché une fois
        verify(gameEvent, times(1)).fire(any(GameEventMessage.class));
    }

    @Test
    public void testPseudo() {
        // Identifiant et pseudo du joueur
        String playerId = "playerId";
        String pseudo = "JohnDoe";
        // Initialise l'état du jeu
        gameService.getInitialState();
        // Le joueur définit son pseudo
        gameService.pseudo(playerId, pseudo);

        // Vérifie que le pseudo du joueur est correctement défini
        assertEquals(pseudo, gameService.getPlayer().getPseudo());
        // Vérifie que l'événement de jeu a été déclenché une fois
        verify(gameEvent, times(1)).fire(any(GameEventMessage.class));
    }

}
