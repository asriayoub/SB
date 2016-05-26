package Game;

import Manager.Creator;

public class RemoteAttack extends Actor {
	protected Actor actor;

	public void actualizeAvatar() {
		avatar.j = position.getJ();
		avatar.i = position.getI();
		avatar.direction = Creator.fromDirectionToString(direction);
		avatar.condition = Creator.fromConditionToString(condition);
		avatar.level = 0;
		avatar.life = 0;
		avatar.lifeMax = 0;
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

	private void hit(int i, int j) {
		direction = Direction.NONE;
		map.getTiles()[position.getI()][position.getJ()] = new Ground();
		map.getBlownAttacks().add(this);
		zone.getAttacks().remove(avatar);

		if (map.getTiles()[position.getI() + i][position.getJ() + j] instanceof Player) {
			Player target = (Player) map.getTiles()[position.getI() + i][position
					.getJ() + j];
			target.getHit(this.actor);
		}
	}

	public void refresh() {
		if (!direction.equals(Direction.NONE)) {
			actualizeAvatar();
			zone.getAttacks().remove(avatar);
			zone = map.locateTileOnZone(this.position);
			zone.getAttacks().add(avatar);
			// System.out.println(position);
			// System.out.println(zone.getPosition());
		}
	}

	@Override
	public String toString() {
		return "RemoteAttack [actor" + actor.name + ", zone=" + zone
				+ ", position=" + position + ", direction=" + direction
				+ ", condition=" + condition + "]";
	}

}
