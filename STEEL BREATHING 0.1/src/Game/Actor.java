package Game;

import Manager.Creator;

public abstract class Actor extends Element {

	protected String name;
	protected Avatar avatar;

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

	public Avatar getAvatar() {
		return avatar;
	}

	public void setAvatar(Avatar avatar) {
		this.avatar = avatar;
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

	public State getState() {
		return state;
	}

	public void setState(State state) {
		this.state = state;
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
			// System.out.println(direction);
			// System.out.println(condition);
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
		actualizeAvatar();
		zone.getPlayers().remove(avatar);
		zone = map.locateTileOnZone(this.position);
		zone.getPlayers().add(avatar);
		// System.out.println(position);
		// System.out.println(zone.getPosition());
	}

	public void actualizeAvatar() {
		avatar.position = position;
		avatar.direction = direction;
		avatar.condition = condition;
		avatar.level = level;
		avatar.life = life;
		avatar.lifeMax = lifeMax;
	}

	public void CreateAvatar(String kind, String name) {
		avatar = new Avatar(kind, name, position, direction, condition, level,
				life, lifeMax);
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
				.equals(Condition.WALKING))
				|| (SecondeInstant - state.getInstant() > 200 && state
						.getReason().equals(Condition.JUMPING))
				|| (SecondeInstant - state.getInstant() > 200 && state
						.getReason().equals(Condition.RUNNING))
				|| (SecondeInstant - state.getInstant() > 150 && state
						.getReason().equals(Condition.STRIKING))
				|| (SecondeInstant - state.getInstant() > 300 && state
						.getReason().equals(Condition.FIRING))
				|| (SecondeInstant - state.getInstant() > 150 && state
						.getReason().equals(Condition.HIT))
				|| state.getReason().equals(Condition.NONE)) {
			state.setActive(true);
			state.setReason(Condition.NONE);
		} else
			state.setActive(false);
		return state.isActive();
	}

	public void act(int i, int j) {
		if (isInMap(position.getI() + i, position.getJ() + j) && isActive()) {
			if (map.getTiles()[position.getI() + i][position.getJ() + j] instanceof Ground
					&& isMovable() && !isAttacking()) {
				// System.out.println("IS MOVING");
				move(i, j);
			} else if (isAttacking()) {
				// System.out.println("IS ATTACKING");
				switch (condition) {
				case STRIKING:
					strike(i, j);
					break;
				case FIRING:
					fire(i, j);
					break;
				default:
					break;
				}
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

	public void fire(int i, int j) {
		if (map.getTiles()[position.getI() + i][position.getJ() + j] instanceof Ground
				|| map.getTiles()[position.getI() + i][position.getJ() + j] instanceof Gap) {
			FireBall fireBall = new FireBall(map, this, new Position(
					position.getI() + i, position.getJ() + j), direction,
					Condition.MOVINGFORWARD, 0, 0, 0);
			fireBall.CreateAvatar(fireBall.getClass().getSuperclass()
					.getSimpleName(), fireBall.getClass().getSimpleName());
			fireBall.loadOnMap();
		}
	}

	public void getHit(Actor opponent) {
		life = life - opponent.power;
		if (life <= 0)
			die();
		setToInactive(condition);
		actualizeAvatar();
	}

	public void die() {
		life = 0;
		condition = Condition.DEAD;
		direction = Direction.NONE;
		map.getTiles()[position.getI()][position.getJ()] = new Ground();
		// zone.getPlayers().remove(avatar);
	}

	public void setToInactive(Condition condition) {
		state.setInstant(System.currentTimeMillis());
		state.setReason(condition);
	}

	public void changeDirection(Direction d) {
		if (d == Direction.UP || d == Direction.DOWN || d == Direction.RIGHT
				|| d == Direction.LEFT || d == Direction.NONE) {
			direction = d;
		}
	}

	public void changeCondition(Condition c) {
		if (c == Condition.RUNNING || c == Condition.WALKING
				|| c == Condition.STANDING || c == Condition.JUMPING
				|| c == Condition.FIRING || c == Condition.STRIKING) {
			condition = c;
		}
	}

	public abstract void loadOnMap();

}
