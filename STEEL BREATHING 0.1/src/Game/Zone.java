package Game;

import java.util.Collection;
import java.util.HashSet;

public class Zone {
	private String name;
	private Position position;

	private int length;

	private Collection<Avatar> players;
	private Collection<Avatar> attacks;

	public Zone() {

	}

	public Zone(String name, Position position) {
		players = new HashSet<Avatar>();
		attacks = new HashSet<Avatar>();
		this.name = name;
		this.position = position;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Position getPosition() {
		return position;
	}

	public void setPosition(Position position) {
		this.position = position;
	}

	public Collection<Avatar> getPlayers() {
		return players;
	}

	public void setPlayers(Collection<Avatar> players) {
		this.players = players;
	}

	public Collection<Avatar> getAttacks() {
		return attacks;
	}

	public void setAttacks(Collection<Avatar> attacks) {
		this.attacks = attacks;
	}

	public int getLength() {
		return length;
	}

	public void setLength(int length) {
		this.length = length;
	}

	public void displayPlayers() {
		System.out.println("players on this zone : { ");
		players.forEach(p -> System.out.println(p.name));
		System.out.println(" }");
	}
}
