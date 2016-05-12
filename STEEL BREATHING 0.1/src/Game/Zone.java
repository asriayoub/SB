package Game;

import java.util.HashMap;
import java.util.Map;

import Client.Avatar;

public class Zone{
	private String name;
	private Position position;

	private int length;

	private Map<String, Avatar> players;

	public Zone() {

	}

	public Zone(String name, Position position) {
		players = new HashMap<String, Avatar>();
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

	public Map<String, Avatar> getPlayers() {
		return players;
	}

	public void setPlayers(Map<String, Avatar> players) {
		this.players = players;
	}

	public int getLength() {
		return length;
	}

	public void setLength(int length) {
		this.length = length;
	}

	public void displayPlayers(){
		System.out.println("players on this zone : { ");
		players.forEach((key,v)-> System.out.println(v.name));
		System.out.println(" }");
	}
}

