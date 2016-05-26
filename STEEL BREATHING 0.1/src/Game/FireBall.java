package Game;

public class FireBall extends RemoteAttack implements Explosible {

	public FireBall(Maps map, Actor actor, Position position,
			Direction direction, Condition condition, int level, int life,
			int lifeMax) {
		this.map = map;
		this.actor = actor;
		this.position = position;
		this.direction = direction;
		this.condition = condition;
		this.level = level;
		this.life = life;
		this.lifeMax = lifeMax;
	}

	@Override
	public void explode() {
		// TODO Auto-generated method stub
	}

}
