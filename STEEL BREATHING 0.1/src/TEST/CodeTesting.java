package TEST;

import java.io.IOException;
import java.util.HashMap;

import Game.Player;

public class CodeTesting {

	public static void main(String[] args) throws IOException {
		HashMap<String,Player> players=new HashMap<>();

		Player p1=new Player();
		Player p2=new Player();
		Player p3=new Player();

		players.put("asri",p1);
		players.put("wadizz",p2);
		players.put("asri",p3);
		
		System.out.println(p1.getClass().getSimpleName());
		System.out.println(players.get("asri"));
		
	}
}
