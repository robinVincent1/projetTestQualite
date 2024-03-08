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

    private GameService gameService;

    @Mock
    private Event<GameEventMessage> gameEvent;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this); // Initialiser les champs annotés par Mock
        gameService = new GameService();
        gameService.setGameEvent(gameEvent); // Injecter l'instance simulée de Event<GameEventMessage>
    }

    @Test
    public void testGetInitialState() {
        String initialState = gameService.getInitialState();
        assertNotNull(initialState);

        Player player = new Player("0", "Pseudo", new ArrayList<>(), 0,null, 100, 0, true, "Menu", false,10,false);
        Dealer dealer = new Dealer(0, false, new ArrayList<>());
        String expectedInitialState = new GameEventMessage("INITIAL_STATE", new GameStateCardsChange(player, dealer)).toJson();

        // Vérifier si l'état initial est une chaîne JSON valide ou contient les bonnes informations
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
                assertTrue(gameService.getPlayer().getIsPlaying());
            }

            // Vérifier si le joueur a dépassé 21
            if (gameService.getPlayer().getScore() > 21) {
                assertEquals("Busted", gameService.getPlayer().getGameStatus());
                assertFalse(gameService.getPlayer().getIsPlaying());
            }

            // Vérifier si le jeu continue
            if (gameService.getPlayer().getScore() < 21) {
                assertTrue(gameService.getPlayer().getIsPlaying());
            }
        }
    }
    @Test
    public void testStandDealerBlackJack() {
        gameService.getInitialState();
        // Le croupier obtient un blackjack
        gameService.getPlayer().setScore(20);
        gameService.getDealer().setScore(21);
        gameService.stand("playerId");
        assertEquals("Loser", gameService.getPlayer().getGameStatus());
        assertFalse(gameService.getPlayer().getIsPlaying());
        verify(gameEvent, times(1)).fire(any(GameEventMessage.class));
    }

    @Test
    public void testStandPlayerTie() {
        gameService.getInitialState();
        // Le joueur obtient un score égal à celui du croupier
        gameService.getPlayer().setScore(18);
        gameService.getDealer().setScore(18);
        gameService.getPlayer().setBet(100);
        gameService.getPlayer().setWallet(0);
        gameService.stand("playerId");
        assertEquals("Tie", gameService.getPlayer().getGameStatus());
        assertEquals(100, gameService.getPlayer().getWallet());
        assertFalse(gameService.getPlayer().getIsPlaying());
        verify(gameEvent, times(1)).fire(any(GameEventMessage.class));
    }

    @Test
    public void testStandPlayerWins() {
        gameService.getInitialState();
        // Le joueur gagne
        gameService.getPlayer().setScore(19);
        gameService.getDealer().setScore(17);
        gameService.getPlayer().setBet(100);
        gameService.getPlayer().setWallet(0);
        gameService.stand("playerId");
        assertEquals("Winner", gameService.getPlayer().getGameStatus());
        assertEquals(200, gameService.getPlayer().getWallet());
        assertFalse(gameService.getPlayer().getIsPlaying());
        verify(gameEvent, times(1)).fire(any(GameEventMessage.class));
    }

    @Test
    public void testStandPlayerLoses() {
        gameService.getInitialState();
        // Le joueur perd
        gameService.getPlayer().setScore(15);
        gameService.getDealer().setScore(20);
        gameService.getPlayer().setBet(100);
        gameService.getPlayer().setWallet(0);
        gameService.stand("playerId");
        assertEquals("Loser", gameService.getPlayer().getGameStatus());
        assertEquals(0, gameService.getPlayer().getWallet());
        assertFalse(gameService.getPlayer().getIsPlaying());
        verify(gameEvent, times(1)).fire(any(GameEventMessage.class));
    }

    @Test
    public void testStandDealerBusted() {
        gameService.getInitialState();
        // Le croupier dépasse 21
        gameService.getPlayer().setScore(18);
        gameService.getDealer().setScore(22);
        gameService.getPlayer().setBet(100);
        gameService.getPlayer().setWallet(0);
        gameService.stand("playerId");
        assertEquals("Winner", gameService.getPlayer().getGameStatus());
        assertEquals(200, gameService.getPlayer().getWallet());
        assertFalse(gameService.getPlayer().getIsPlaying());
        verify(gameEvent, times(1)).fire(any(GameEventMessage.class));
    }

    @Test
    public void testStandInsuranceWinner() {
        gameService.getInitialState();
        // Le croupier obtient un blackjack et le joueur a pris une assurance
        gameService.getPlayer().setAssurance(true);
        gameService.getPlayer().setScore(20);
        gameService.getDealer().setScore(21);
        gameService.getPlayer().setBet(100);
        gameService.getPlayer().setWallet(0);
        gameService.stand("playerId");
        assertEquals("InsuranceWinner", gameService.getPlayer().getGameStatus());
        assertEquals(200, gameService.getPlayer().getWallet());
        assertFalse(gameService.getPlayer().getIsPlaying());
        verify(gameEvent, times(1)).fire(any(GameEventMessage.class));
    }

    @Test
    public void testBet() {
        gameService.getInitialState();
        gameService.bet("playerId", "50");
        assertEquals(50, gameService.getPlayer().getBet());
        assertEquals(50, gameService.getPlayer().getWallet());
        assertEquals("BetDone", gameService.getPlayer().getGameStatus());
        verify(gameEvent, times(1)).fire(any(GameEventMessage.class));
    }

    @Test
    public void testReload() {
        gameService.getInitialState();
        gameService.reload("playerId");

        assertEquals("Menu", gameService.getPlayer().getGameStatus());
        assertEquals(0, gameService.getPlayer().getBet());
        assertNull(gameService.getPlayer().getAltScore());
        assertEquals(10, gameService.getPlayer().getClock());
        assertFalse(gameService.getPlayer().getIsStanding());
        assertTrue(gameService.getPlayer().getHand().isEmpty());
        assertEquals(0, gameService.getPlayer().getScore());

        assertTrue(gameService.getDealer().getHand().isEmpty());
        assertEquals(0, gameService.getDealer().getScore());

        verify(gameEvent, times(1)).fire(any(GameEventMessage.class));
    }

    @Test
    public void testPseudo() {
        String playerId = "playerId";
        String pseudo = "JohnDoe";
        gameService.getInitialState();
        gameService.pseudo(playerId, pseudo);

        assertEquals(pseudo, gameService.getPlayer().getPseudo());
        verify(gameEvent, times(1)).fire(any(GameEventMessage.class));
    }
}
