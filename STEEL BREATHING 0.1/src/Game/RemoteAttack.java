package Game;

import Client.Avatar;

public abstract class RemoteAttack extends Element {
	protected Avatar avatar;
	protected Actor actor;

	protected Maps map;
	protected Zone zone;
	protected Position position;

	protected Direction direction;
	protected Condition condition;

	public void loadOnMap(Maps m) {
		if (zone == null) {
			m.getTiles()[position.getI()][position.getJ()] = this;
			zone = m.locateTileOnZone(position);
			zone.getAttacks().add(avatar);
			map.getRemoteAttacks().add(this);
		}
	}

	public void progress() {
		switch (direction) {
		case UP:
			progress(-1, 0);
			break;
		case DOWN:
			progress(+1, 0);
			break;
		case LEFT:
			progress(0, -1);
			break;
		case RIGHT:
			progress(0, +1);
			break;
		default:
			break;
		}
		refresh();
	}

	private void progress(int i, int j) {
		if (map.getTiles()[position.getI() + i][position.getJ() + j] instanceof Ground) {
			move(i, j);
		} else
			hit(i, j);
	}

	private void move(int i, int j) {
		map.getTiles()[position.getI()][position.getJ()] = new Ground();
		map.getTiles()[position.getI() + i][position.getJ() + j] = this;
		position = new Position(position.getI() + i, position.getJ() + j);
		avatar.setPosition(position.getI(), position.getJ());
	}

	private void hit(int i, int j) {
		direction = Direction.NONE;
		map.getTiles()[position.getI()][position.getJ()] = new Ground();
		map.getRemoteAttacks().remove(this);
		zone.getAttacks().remove(avatar);
	}

	public void refresh() {
		if (!direction.equals(Direction.NONE)) {
			zone.getAttacks().remove(avatar);
			zone = map.locateTileOnZone(this.position);
			zone.getAttacks().add(avatar);
			// System.out.println(position);
			//System.out.println(zone.getPosition());
		}
	}

	@Override
	public String toString() {
		return "RemoteAttack [actor" + actor.name + ", zone="
				+ zone + ", position=" + position + ", direction=" + direction
				+ ", condition=" + condition + "]";
	}

}
