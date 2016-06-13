package Game;

import java.io.Serializable;

public abstract class Element implements Serializable {
	private static final long serialVersionUID = 1L;
	protected Position position;
	
	public Position getPosition() {
		return position;
	}
	public void setPosition(Position position) {
		this.position = position;
	}
	
	
}
