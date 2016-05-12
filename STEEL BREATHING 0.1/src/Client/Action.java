package Client;

import java.io.Serializable;

import Game.Condition;
import Game.Direction;

public class Action implements Serializable {

	private static final long serialVersionUID = 1L;

	public Direction direction;
	public Condition condition;

	public Action(Direction direction, Condition condition) {
		super();
		this.direction = direction;
		this.condition = condition;
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

	@Override
	public String toString() {
		return "Action [direction=" + direction + ", condition=" + condition
				+ "]";
	}

}
