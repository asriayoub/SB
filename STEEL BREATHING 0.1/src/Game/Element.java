package Game;

import java.io.Serializable;

public abstract class Element implements Serializable{
	private static final long serialVersionUID = 1L;
	protected Position position;
	

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
		switch (d) {
		case UP:
			return "UP";
		case DOWN:
			return "DOWN";
		case RIGHT:
			return "RIGHT";
		case LEFT:
			return "LEFT";
		case NONE:
			return "NONE";
		default:
			return "";
		}
	}
}
