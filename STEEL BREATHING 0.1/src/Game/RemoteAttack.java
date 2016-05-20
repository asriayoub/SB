package Game;

import Client.Avatar;

public abstract class RemoteAttack extends Element {
	protected String owner;
	protected Actor actor;

	protected Zone zone;
	protected Position position;

	protected Direction direction;
	protected Condition condition;

	public void loadOnMap(Maps m) {
		if (zone == null) {
			m.getTiles()[position.getI()][position.getJ()] = this;
			zone = m.locateTileOnZone(position);
			zone.getAttacks().put(
					position,
					new Avatar(this.getClass().getSimpleName(), this.getClass()
							.getSuperclass().getSimpleName(), position.getJ(),
							position.getI(),
							fromDirectionToString(actor.direction), "NONE",
							actor.level, 0, 0));
			actor.map.getRemoteAttacks().put(position, this);
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

		if (actor.map.getTiles()[position.getI() + i][position.getJ() + j] instanceof Ground) {
			move(i, j);
		} else
			hit(i, j);
	}

	private void move(int i, int j) {
		System.out.println(position);
		actor.map.getTiles()[position.getI()][position.getJ()] = new Ground();
		actor.map.getTiles()[position.getI() + i][position.getJ() + j] = this;
		position = new Position(position.getI() + i, position.getJ() + j);
		System.out.println(position);
	}

	private void hit(int i, int j) {

	}

	public void refresh() {
		zone.getAttacks().remove(position);
		zone = actor.map.locateTileOnZone(this.position);
		zone.getAttacks()
				.put(position,
						new Avatar(this.getClass().getSimpleName(), this
								.getClass().getSuperclass().getSimpleName(),
								position.getJ(), position.getI(),
								fromDirectionToString(direction),
								"NONE", actor.level, 0,
								0));
		// System.out.println(position);
		// System.out.println(zone.getPosition());
	}

	@Override
	public String toString() {
		return "RemoteAttack [owner=" + owner + ", actor=" + actor + ", zone="
				+ zone + ", position=" + position + ", direction=" + direction
				+ ", condition=" + condition + "]";
	}

}
