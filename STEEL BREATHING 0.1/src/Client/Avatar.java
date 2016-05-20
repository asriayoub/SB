package Client;

import java.io.Serializable;

import Game.Element;

public class Avatar extends Element implements Serializable {
	private static final long serialVersionUID = 1L;

	public String kind;
	public String name;

	public int j;
	public int i;

	public String direction;
	public String condition;

	int level;

	int life;
	int lifeMax;

	public Avatar() {

	}

	public Avatar(String kind, String name, int j, int i, String direction,
			String condition, int level, int life, int lifeMax) {
		super();
		this.kind=kind;
		this.name = name;
		this.j = j;
		this.i = i;
		this.direction = direction;
		this.condition = condition;
		this.level = level;
		this.life = life;
		this.lifeMax = lifeMax;
	}

	@Override
	public String toString() {
		return "Avatar [name=" + name + ", j=" + j + ", i=" + i
				+ ", direction=" + direction + ", condition=" + condition
				+ ", level=" + level + ", life=" + life + ", lifeMax="
				+ lifeMax + "]";
	}

}
