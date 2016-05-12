package Game;

import Client.Avatar;

public abstract class Actor extends Element {

	protected String name;

	protected Maps map;
	protected Zone zone;

	protected Direction direction;
	protected Condition condition;
	protected State state;

	protected int level;

	protected int life;
	protected int lifeMax;

	protected int power;
	protected int speed;

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

	public Zone getZone() {
		return zone;
	}

	public void setZone(Zone zone) {
		this.zone = zone;
	}

	public Direction getDirection() {
		return direction;
	}

	public void setDirection(Direction direction) {
		this.direction = direction;
	}

	public Condition getCondition() {
		return condition;
	}

	public void setCondition(Condition condition) {
		this.condition = condition;
	}

	public int getLevel() {
		return level;
	}

	public void setLevel(int level) {
		this.level = level;
	}

	public int getLife() {
		return life;
	}

	public void setLife(int life) {
		this.life = life;
	}

	public int getLifeMax() {
		return lifeMax;
	}

	public void setLifeMax(int lifeMax) {
		this.lifeMax = lifeMax;
	}

	public int getPower() {
		return power;
	}

	public void setPower(int power) {
		this.power = power;
	}

	public int getSpeed() {
		return speed;
	}

	public void setSpeed(int speed) {
		this.speed = speed;
	}

	@Override
	public String toString() {
		return "Actor [name=" + name + ", map=" + map + ", zone=" + zone
				+ ", direction=" + direction + ", condition=" + condition
				+ ", level=" + level + ", life=" + life + ", lifeMax="
				+ lifeMax + ", power=" + power + ", speed=" + speed + "]";
	}

	public void act(Direction d, Condition c) {
		if (isActive()) {
			changeCondition(c);
			if (c != Condition.JUMPING)
				changeDirection(d);
			System.out.println(direction);
			System.out.println(condition);
			for (int k = 0; k < c.getValue(); k++)
				switch (d) {
				case UP:
					act(-1, 0);
					break;
				case DOWN:
					act(+1, 0);
					break;
				case LEFT:
					act(0, -1);
					break;
				case RIGHT:
					act(0, +1);
					break;
				default:
					break;
				}
			setToInactive(c);
			refresh();
		}
	}

	public void refresh() {
		zone.getPlayers().remove(name);
		zone = map.locateTileOnZone(this.position);
		zone.getPlayers()
				.put(name,
						new Avatar(name, position.getJ(), position.getI(),
								fromDirectionToString(direction),
								fromConditionToString(condition), level, life,
								lifeMax));
		System.out.println(position);
		System.out.println(zone.getPosition());
	}

	private boolean isInMap(int i, int j) {
		return position.getJ() > 0 && position.getJ() < map.getLength() - 1
				&& position.getI() > 0 && position.getI() < map.getLength() - 1;
	}

	public boolean isMovable() {
		return condition == Condition.RUNNING || condition == Condition.WALKING
				|| condition == Condition.JUMPING;
	}

	public boolean isAttacking() {
		return condition == Condition.FIRING || condition == Condition.STRIKING;
	}

	public boolean isActive() {
		long SecondeInstant = System.currentTimeMillis();
		if ((SecondeInstant - state.getInstant() > 200 && state.getReason()
				.equals("WALKING"))
				|| (SecondeInstant - state.getInstant() > 200 && state
						.getReason().equals("JUMPING"))
				|| (SecondeInstant - state.getInstant() > 200 && state
						.getReason().equals("RUNNING"))
				|| (SecondeInstant - state.getInstant() > 150 && state
						.getReason().equals("STRIKING"))
				|| (SecondeInstant - state.getInstant() > 300 && state
						.getReason().equals("FIRING"))
				|| (SecondeInstant - state.getInstant() > 150 && state
						.getReason().equals("HIT"))
				|| state.getReason().equals("NONE")) {
			state.setActive(true);
			state.setReason("NONE");
		} else
			state.setActive(false);
		return state.isActive();
	}

	public void act(int i, int j) {
		if (isInMap(position.getI() + i, position.getJ() + j) && isActive()) {
			if (map.getTiles()[position.getI() + i][position.getJ() + j] instanceof Ground
					&& isMovable() && !isAttacking()) {
				System.out.println("IS MOVING");
				move(i, j);
			} else if (isAttacking()) {
				System.out.println("IS ATTACKING");
				strike(i, j);
			}
		}
	}

	public void move(int i, int j) {
		map.getTiles()[position.getI()][position.getJ()] = new Ground();
		map.getTiles()[position.getI() + i][position.getJ() + j] = this;
		position = new Position(position.getI() + i, position.getJ() + j);
	}

	public void strike(int i, int j) {
		if (map.getTiles()[position.getI() + i][position.getJ() + j] instanceof Player) {
			Actor opponent = (Actor) map.getTiles()[position.getI() + i][position
					.getJ() + j];
			opponent.getHit(this);
			opponent.setToInactive(Condition.HIT);
			opponent.refresh();
		}
	}

	public void getHit(Actor opponent) {
		life = life - opponent.power;
		if (life <= 0) {
			life = 0;
			condition = Condition.DEAD;
			direction = Direction.NONE;
		}
	}

	public void setToInactive(Condition condition) {
		state.setInstant(System.currentTimeMillis());
		state.setReason(fromConditionToString(condition));
	}

	public void changeDirection(Direction d) {
		if (d == Direction.UP || d == Direction.DOWN || d == Direction.RIGHT
				|| d == Direction.LEFT || d == Direction.NONE)
			direction = d;
	}

	public void changeCondition(Condition c) {
		if (c == Condition.RUNNING || c == Condition.WALKING
				|| c == Condition.STANDING || c == Condition.JUMPING
				|| c == Condition.FIRING || c == Condition.STRIKING)
			condition = c;
	}

	public void loadOnMap(Maps m) {
		if (zone == null) {
			m.getTiles()[position.getI()][position.getJ()] = this;
			zone = m.locateTileOnZone(position);
			zone.getPlayers().put(
					name,
					new Avatar(name, position.getJ(), position.getI(), "NONE",
							"NONE", level, life, lifeMax));
		}
	}

	public String fromConditionToString(Condition e) {
		switch (e) {
		case STANDING:
			return "STANDING";
		case RUNNING:
			return "RUNNING";
		case WALKING:
			return "WALKING";
		case JUMPING:
			return "JUMPING";
		case UNREADY:
			return "UNREADY";
		case FIRING:
			return "FIRING";
		case STRIKING:
			return "STRIKING";
		case DEAD:
			return "DEAD";
		case HIT:
			return "HIT";
		default:
			return "";
		}
	}

	public String fromDirectionToString(Direction d) {
		String condition = "";
		switch (d) {
		case UP:
			condition = "UP";
			break;
		case DOWN:
			condition = "DOWN";
			break;
		case RIGHT:
			condition = "RIGHT";
			break;
		case LEFT:
			condition = "LEFT";
			break;
		case NONE:
			condition = "NONE";
			break;
		default:
			break;
		}
		return condition;
	}
}
