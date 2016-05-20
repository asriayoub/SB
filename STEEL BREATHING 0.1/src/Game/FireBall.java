package Game;

public class FireBall extends RemoteAttack implements Explosible{

	public FireBall(String owner, Actor actor, Position position, Direction direction) {
		this.owner=owner;
		this.actor=actor;
		this.position=position;
		this.direction=direction;
	}

	@Override
	public void explode() {
		// TODO Auto-generated method stub
	}

}
