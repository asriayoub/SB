package NPC;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import Game.Condition;
import Game.Direction;

public class Spamer implements Runnable {
	NPC npc;
	List<Condition> conditions;
	List<Direction> directions;

	public Spamer(NPC npc) {
		this.npc = npc;
		directions = new ArrayList<>();
		conditions = new ArrayList<>();
		directions.add(Direction.LEFT);
		directions.add(Direction.RIGHT);
		directions.add(Direction.UP);
		directions.add(Direction.DOWN);

		conditions.add(Condition.RUNNING);
		conditions.add(Condition.WALKING);
		conditions.add(Condition.FIRING);
		conditions.add(Condition.JUMPING);
		conditions.add(Condition.STANDING);
		conditions.add(Condition.STRIKING);
		conditions.add(Condition.HIT);
		conditions.add(Condition.MOVINGFORWARD);
	}

	@Override
	public void run() {

		long firstInstant = System.currentTimeMillis();
		long SecondeInstant;
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

			SecondeInstant = System.currentTimeMillis();
			if (SecondeInstant - firstInstant > 20000) {
				r = rand.nextInt(4);
				npc.condition = conditions.get(r);
			} else {
				npc.condition = Condition.RUNNING;
			}
			npc.SendMove();
		}
	}

}
