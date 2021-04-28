package tests;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;

import components.Card;
import components.Deck;
import components.Game;
import components.Hand;
import components.Player;

public class Test4 {

	private static int deckCount = 1;

	public static void main(String[] args) {
		Deck d = new Deck(deckCount);
		shuffleDeck(d, 3);
		Player player = new Player("Gregg", 100.0);
		playGame(d, player);

	}

	// ^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

	public static void shuffleDeck(Deck d, int n) {
		for (int i = 0; i < n; i++) {
			Collections.shuffle(d.getDeck());
		}
	}

	public static void playGame(Deck d, Player player) {
		Scanner sc = new Scanner(System.in);
		int handCount = 4;
		int cardsPerHand = 2;

		double bet = 10.0;
		double totalBet = bet * (handCount - 1);
		Game game = new Game(1, player, totalBet);

		Hand[] hands = deal(d, handCount, cardsPerHand, bet);
		player.setHands(setPlayerHands(hands, handCount));
		Hand dealerHand = hands[hands.length - 1];

		System.out.println("You are currently playing " + (handCount - 1) + " hands!");
		System.out.println("You are betting: " + bet + " per hand\nTotal Bet: " + totalBet + "\n");
		System.out.println(player.getName() + "'s hands are:");

		afterDealCheck(player, dealerHand);

		System.out.println("\nDealer's showing:\n" + dealerHand.getHand().get(0));

		if (dealerHand.hasBlackJack()) {
			System.out.println("Dealer has BlackJack!");
			if (loseFast(player.getHands())) {
				System.out.println("Game Over");
				game.setActive(false);
				runItBack(d, game, player, dealerHand);
			}
		}

		playerTurn(d, player, sc);
		System.out.println("Dealer's hand is:\n" + dealerHand + "\n");
		dealerHand.dealerTurn(d);
		showDown(player, dealerHand);

		// not sure if i need dealerHand here or not.....
		runItBack(d, game, player, dealerHand);
	}

	public static void runItBack(Deck d, Game game, Player player, Hand dealerHand) {
		System.out.println("Would you like to play again?");
		String ans = new Scanner(System.in).nextLine().toLowerCase();
		if (ans.equals("yes")) {
			player.getHands().clear();
			dealerHand.getHand().clear();
			int cardsLeft = d.getDeck().size();
			Deck dd = new Deck(deckCount);
			System.out.println("\n**************************NEW GAME***********************************\n");
			System.out.println("Cards left: " + cardsLeft);
			if (cardsLeft < 25) {
				System.out.println("New deck coming in!!!");
				d = dd;
				shuffleDeck(d, 3);
			}
			playGame(d, player);
		} else {
			System.out.println("Thanks for playing!");
			System.exit(0);
		}
	}

	public static void afterDealCheck(Player player, Hand dealerHand) {
		int counter = 1;
		for (int i = 0; i < player.getHands().size(); i++) {
			Hand h = player.getHands().get(i);
			if (h.hasBlackJack() && !dealerHand.hasBlackJack()) {
				h.setBjack(true);
				h.setStatus(1);
				System.out.println("Hand " + counter + " is: " + h);
			} else if (!h.hasBlackJack() && dealerHand.hasBlackJack()) {
				h.setStatus(2);
				dealerHand.setBjack(true);
				System.out.println("Hand " + counter + " is: " + h);
			} else if (h.hasBlackJack() && dealerHand.hasBlackJack()) {
				push(h, player);
				h.setStatus(3);
				System.out.println("Hand " + counter + " is: " + h);
			} else {
				System.out.println("Hand " + counter + " is: " + h);
			}
			counter++;
		}
	}

	public static void playerTurn(Deck d, Player player, Scanner sc) {
		List<Hand> activeHands = getActiveHands(player.getHands());
		System.out.println();
		for (Hand h : activeHands) {
			int index = player.getHands().indexOf(h);
			System.out.println(h);
			handOptions(h, player);
			int choice = sc.nextInt();
			playerOption(d, h, player, choice, sc, index);
		}
	}

	public static void showDown(Player player, Hand dealerHand) {
		int dv = 21 - dealerHand.getValue();
		int counter = 1;
		for (int i = 0; i < player.getHands().size(); i++) {
			Hand h = player.getHands().get(i);
			int pv = 21 - h.getValue();
			System.out.println("Hand " + counter + ": " + h);
			if (h.getStatus() == 2) {
				lose(h, player);
			} else if (h.getStatus() == 1) {
				win(h, player);
			} else if (dv >= 0 && pv < dv || dealerHand.busted()) {
				win(h, player);
			} else if (dv == pv) {
				push(h, player);
			} else {
				lose(h, player);
			}
			counter++;
		}
	}

	public static void playerOption(Deck d, Hand h, Player player, int choice, Scanner sc, int handIndex) {
		boolean keepGoing = true;
		while (keepGoing) {
			while (choice == 1) {
				h.hit(d);
				if (h.busted()) {
					System.out.println("Player has busted!\n");
					h.setStatus(2);
					keepGoing = false;
					break;
				} else {
					System.out.println("What would you like to do?\n1.hit\n2.stand\n");
					choice = sc.nextInt();
				}
			}

			if (choice == 2) {
				System.out.println("Player stands!\n");
				keepGoing = false;
				break;
			}

			if (choice == 3) {
				double dblBet = h.getBet() * 2;
				h.setBet(dblBet);
				System.out.println("Player had doubled down!");
				h.hit(d);
				keepGoing = false;
			}

			if (choice == 4) {
				System.out.println("\nPlayer's first split hand is:");
				int index = handIndex + 1;
				Hand h2 = h.splitHand();
				h2.setBet(h.getBet());
				h.hit(d);
				handOptions(h, player);
				choice = sc.nextInt();
				playerOption(d, h, player, choice, sc);
				System.out.println("\nPlayer's second split hand is:");
				h2.hit(d);
				player.getHands().add(index, h2);
				handOptions(h2, player);
				choice = sc.nextInt();
				playerOption(d, h2, player, choice, sc);
			}
		}
	}

	public static void playerOption(Deck d, Hand h, Player player, int choice, Scanner sc) {
		boolean keepGoing = true;
		while (keepGoing) {
			while (choice == 1) {
				h.hit(d);
				if (h.busted()) {
					System.out.println("Player has busted!\n");
					h.setStatus(2);
					keepGoing = false;
					break;
				} else {
					System.out.println("What would you like to do?\n1.hit\n2.stand\n");
					choice = sc.nextInt();
				}
			}

			if (choice == 2) {
				System.out.println("Player stands!\n");
				keepGoing = false;
				continue;
			}

			if (choice == 3) {
				double dblBet = h.getBet() * 2;
				h.setBet(dblBet);
				System.out.println("Player had doubled down!");
				h.hit(d);
				keepGoing = false;
			}

			if (choice == 4) {
				System.out.println("\nPlayer's first split hand is:");
				Hand h2 = h.splitHand();
				h2.setBet(h.getBet());
				h.hit(d);
				handOptions(h, player);
				choice = sc.nextInt();
				playerOption(d, h, player, choice, sc);
				System.out.println("\nPlayer's second split hand is:");
				h2.hit(d);
				player.getHands().add(h2);
				handOptions(h2, player);
				choice = sc.nextInt();
				playerOption(d, h2, player, choice, sc);
			}
		}
	}

	public static void handOptions(Hand hand, Player player) {
		double bet = hand.getBet();
		boolean keepGoing = true;

		while (keepGoing) {
			if (hand.canDouble(player.getBalance(), bet) && hand.canSplit()) {
				System.out.println("What would you like to do?\n1. hit\n2. stand\n3. double\n4. split\n");
				break;
			} else if (hand.canDouble(player.getBalance(), bet)) {
				System.out.println("What would you like to do?\n1. hit\n2. stand\n3. double\n");
				break;
			} else {
				System.out.println("What would you like to do?\n1. hit\n2. stand\n");
				break;
			}
		}
	}

	public static List<Hand> getActiveHands(List<Hand> playerHands) {
		List<Hand> activeHands = new ArrayList<>();
		for (Hand h : playerHands) {
			if (h.getStatus() == 0) {
				activeHands.add(h);
			}
		}
		return activeHands;
	}

	public static boolean loseFast(List<Hand> playerHands) {
		int count = 0;
		for (Hand h : playerHands) {
			if (h.getStatus() == 2) {
				count++;
			}
		}
		if (count == 4) {
			return true;
		}
		return false;
	}

	public static void win(Hand playerHand, Player player) {
		double bet = playerHand.getBet();
		if (playerHand.isBjack()) {
			bet = playerHand.getBet() * 1.5;
			System.out.println("Player has BlackJack!");
		}
		double currentBalance = player.getBalance();
		double newBal = currentBalance + bet;
		player.setBalance(newBal);
		playerHand.setWin(true);
		System.out.println("Player's hand wins: " + bet);
		System.out.println("Updated balance is: " + player.getBalance() + "\n");
	}

	public static void lose(Hand playerHand, Player player) {
		double bet = playerHand.getBet();
		double currentBalance = player.getBalance();
		double newBal = currentBalance - bet;
		player.setBalance(newBal);
		playerHand.setWin(false);
		System.out.println("Player's hand loses: " + bet);
		System.out.println("Updated balance is: " + player.getBalance() + "\n");
	}

	public static void push(Hand playerHand, Player player) {
		System.out.println("It's a push!\n");
	}

	public static List<Hand> setPlayerHands(Hand[] hands, int handCount) {
		List<Hand> playerHands = new ArrayList<>();
		for (int i = 0; i < handCount; i++) {
			Hand h = hands[i];
			if (i < handCount - 1) {
				playerHands.add(h);
			}
		}
		return playerHands;
	}

	public static Hand[] deal(Deck d, int handCount, int cardsPerHand, double bet) {
		List<Card> dealCards = getDealCards(d, handCount, cardsPerHand);
		Hand[] hands = new Hand[handCount];
		for (int i = 0; i < handCount; i++) {
			hands[i] = new Hand(0);
			List<Card> h = hands[i].getHand();
			int index = i + handCount;
			Card c1 = dealCards.get(i);
			Card c2 = dealCards.get(index);
			h.add(c1);
			h.add(c2);
			hands[i].calcValue();
			hands[i].setBet(bet);
		}
		return hands;
	}

	public static List<Card> getDealCards(Deck d, int playerCount, int cardsPer) {
		List<Card> cards = new ArrayList<>();
		for (int i = 0; i < playerCount * cardsPer; i++) {
			Card c = d.drawCard();
			cards.add(c);
		}
		return cards;
	}

	public static void printDeck(Deck d) {
		for (int i = 0; i < d.getDeck().size(); i++) {
			if (i % 4 == 0) {
				System.out.println();
			}
			System.out.print(d.getDeck().get(i) + "|");
		}
	}

	// *********************************************************************
	public static int countFaceCards(Deck d) {
		int count = 0;
		List<Card> cards = d.getDeck();
		for (Card c : cards) {
			if (c.getValue() == 10) {
				count++;
			}
		}
		return count;
	}

}
