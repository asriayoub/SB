package Game;

public enum Condition {

	STANDING(0), RUNNING(2), WALKING(1), JUMPING(1), FIRING(0), UNREADY(0), DEAD(
			0), STRIKING(1), HIT(0);

	private int value;

	private Condition(int value) {
		this.value = value;
	}

	public int getValue() {
		return value;
	}
}
