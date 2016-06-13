package Game;

public class State {
	private long instant;
	private boolean active; // INACTIVE // ACTIVE
	private Condition reason; // STRIKING //HIT //FIRING //DEATH //START //NONE

	public State() {
		this.instant = System.currentTimeMillis();
		this.active = true;
		this.reason = Condition.NONE;
	}

	public long getInstant() {
		return instant;
	}

	public void setInstant(long instant) {
		this.instant = instant;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public Condition getReason() {
		return reason;
	}

	public void setReason(Condition reason) {
		this.reason = reason;
	}

}
