package Game;

public class FireBall extends RemoteAttack implements Explosible{

	public FireBall(Maps map, Actor actor, Position position, Direction direction) {
		this.map=map;
		this.actor=actor;
		this.position=position;
		this.direction=direction;
	}

	@Override
	public void explode() {
		// TODO Auto-generated method stub
	}

}
