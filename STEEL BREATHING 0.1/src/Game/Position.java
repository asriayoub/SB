package Game;

import java.io.Serializable;

public class Position implements Serializable {
	private static final long serialVersionUID = 1L;

	private int i;
	private int j;

	public Position() {

	}

	public Position(int i, int j) {
		this.i = i;
		this.j = j;
	}

	public int getI() {
		return i;
	}

	public void setI(int i) {
		this.i = i;
	}

	public int getJ() {
		return j;
	}

	public void setJ(int j) {
		this.j = j;
	}

	@Override
	public String toString() {
		return "[" + i + "][" + j + "]";
	}

}
