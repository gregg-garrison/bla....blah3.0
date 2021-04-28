package components;

import java.util.ArrayList;
import java.util.List;

public class Hand {
	private List<Card> hand = new ArrayList<>();
	private int value;
	private boolean win;
	private double bet;
	private Game game;
	private Player player;
	private boolean bjack;
	private int status;

	public Hand() {
	};
	
	public Hand(int status) {
		this.status = status;
	}

	public Hand(int value, List<Card> hand) {
		this.value = value;
		this.hand = hand;
	}
	
	public Hand(int value, List<Card> hand, double bet) {
		this.value = value;
		this.hand = hand;
		this.bet = bet;
	}
	
	

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((hand == null) ? 0 : hand.hashCode());
		result = prime * result + value;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Hand other = (Hand) obj;
		if (hand == null) {
			if (other.hand != null)
				return false;
		} else if (!hand.equals(other.hand))
			return false;
		if (value != other.value)
			return false;
		return true;
	}

	public List<Card> getHand() {
		return hand;
	}

	public void setHand(List<Card> hand) {
		this.hand = hand;
	}

	public int getValue() {
		return value;
	}

	public void setValue(int value) {
		this.value = value;
	}
	
	public boolean isWin() {
		return win;
	}

	public void setWin(boolean win) {
		this.win = win;
	}

	public double getBet() {
		return bet;
	}

	public void setBet(double bet) {
		this.bet = bet;
	}

	public Game getGame() {
		return game;
	}

	public void setGame(Game game) {
		this.game = game;
	}

	public Player getPlayer() {
		return player;
	}

	public void setPlayer(Player player) {
		this.player = player;
	}

	public boolean isBjack() {
		return bjack;
	}

	public void setBjack(boolean bjack) {
		this.bjack = bjack;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	@Override
	public String toString() {
//		return hand + " | " + value + " | BlackJack: "+this.isBjack()+" | Status: "+this.getStatus()+" | Bet: "+bet;
		return hand + " | " + value;
	}

	public int hit(Deck d) {
		int newVal = 0;
		Card c = d.drawCard();
		System.out.println(c);
		this.getHand().add(c);
		newVal = this.calcValue();
		System.out.println(this+"\n");

		return newVal;
	}

	public int calcValue() {
		int val = 0;
		int aceCount = 0;

		for (int i = 0; i < this.hand.size(); i++) {
			Card c = hand.get(i);
			val += c.getValue();
			if (c.getValue() == 11) {
				aceCount++;
			}
			while (aceCount > 0 && val > 21) {
				val -= 10;
				aceCount--;
			}
		}
		this.setValue(val);
		return val;
	}
	
	public int dealerTurn(Deck d) {
		int newVal = 0;
		while(this.dealerHit() && !this.busted()) {
			System.out.println("Dealer hits!");
			newVal = this.hit(d);
		}
		if(this.busted()) {
			System.out.println("Dealer Busted!\n");
		}
		return newVal;
	}
	
	public Hand splitHand(){
		Hand h = new Hand();
		List<Card> split = new ArrayList<>();
		Card c1 = this.hand.remove(1);
		split.add(c1);
		h.setHand(split);
		this.calcValue();
		h.calcValue();
		
		return h;
	}
	
	public boolean busted() {
		if(this.getValue()>21) {
			return true;
		}
		return false;
	}
	
	public boolean hasBlackJack() {
		if(this.getValue()==21) {
			return true;
		}
		return false;
	}
	
	public boolean dealerHit() {
		if(!this.busted()&&this.getValue()<17) {
			return true;
		}
		return false;
	}
	
	public boolean canSplit() {
		if(this.getHand().get(0).getValue()==this.getHand().get(1).getValue()) {
			return true;
		}
		return false;
	}
	
	public boolean canDouble(double balance, double bet) {
		if(balance >= bet * 2) {
			return true;
		}
		return false;
	}
}
