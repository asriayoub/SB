package TEST;

import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;

import Game.Player;

public class CodeTesting {

	public static void main(String[] args) throws IOException {
		Collection<Player> players=new HashSet<>();
		Player p1=new Player();
		Player p2=new Player();
		Player p3=new Player();

		players.add(p1);
		players.add(p2);
		players.add(p3);
		
		System.out.println(players.size());
		System.out.println(players.remove(p1));
		System.out.println(players.size());
	}
}
