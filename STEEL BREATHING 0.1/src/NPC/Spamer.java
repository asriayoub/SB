package NPC;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import Game.Condition;
import Game.Direction;

public class Spamer implements Runnable {
	NPC npc;
	// List<Condition> conditions;
	List<Direction> directions;

	public Spamer(NPC npc) {
		this.npc = npc;
		directions = new ArrayList<>();
		directions.add(Direction.LEFT);
		directions.add(Direction.RIGHT);
		directions.add(Direction.UP);
		directions.add(Direction.DOWN);
	}

	@Override
	public void run() {
		int r;
		Random rand;
		while (true) {
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			rand = new Random();
			r = rand.nextInt(4);
			npc.direction = directions.get(r);
			npc.condition = Condition.RUNNING;
			npc.SendMove();
		}
	}

}
