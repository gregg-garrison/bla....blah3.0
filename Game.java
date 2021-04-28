package components;

public class Game {
	
	private int gameId;
	private Player player;
	private double totalBet;
	private boolean active;
	
	
	public Game() {};
	
	public Game(int gameId, Player player, double totalBet) {
		this.gameId = gameId;
		this.player = player;
		this.totalBet = totalBet;
	}

	public int getGameId() {
		return gameId;
	}

	public void setGameId(int gameId) {
		this.gameId = gameId;
	}

	public Player getPlayer() {
		return player;
	}

	public void setPlayer(Player player) {
		this.player = player;
	}

	public double getTotalBet() {
		return totalBet;
	}

	public void setTotalBet(double totalBet) {
		this.totalBet = totalBet;
	}
	
	
	
	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	@Override
	public String toString() {
		return this.getPlayer().getName()+"\n"+this.getPlayer().getHands().toString();	
	}
	

}
