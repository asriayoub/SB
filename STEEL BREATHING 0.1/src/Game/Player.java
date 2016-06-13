package Game;

public class Player extends Actor {

	private int experience;

	public Player(String name, Position position, Maps map, int life,
			int lifeMax, int power, State state) {
		this.name = name;
		this.position = position;
		this.map = map;
		this.direction = Direction.NONE;
		this.condition = Condition.STANDING;
		this.life = life;
		this.lifeMax = lifeMax;
		this.power = power;
		this.state = state;
	}

	public Player() {
	}

	public int getExperience() {
		return experience;
	}

	public void setExperience(int experience) {
		this.experience = experience;
	}

	public void loadOnMap() {
		map.getTiles()[position.getI()][position.getJ()] = this;
		zone = map.locateTileOnZone(position);
		zone.getPlayers().add(avatar);
	}

}
