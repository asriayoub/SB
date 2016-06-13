package Game;

import java.io.Serializable;

public class Avatar extends Element implements Serializable {
	private static final long serialVersionUID = 1L;

	public String kind;
	public String name;

	public Direction direction;
	public Condition condition;

	public int level;

	public int life;
	public int lifeMax;

	public Avatar() {

	}

	public Avatar(String kind, String name, Position position,
			Direction direction, Condition condition, int level, int life, int lifeMax) {
		this.position = position;
		this.kind = kind;
		this.name = name;
		this.direction = direction;
		this.condition = condition;
		this.level = level;
		this.life = life;
		this.lifeMax = lifeMax;
	}

	@Override
	public String toString() {
		return "Avatar [kind=" + kind + ", name=" + name + ", direction="
				+ direction + ", condition=" + condition + ", level=" + level
				+ ", life=" + life + ", lifeMax=" + lifeMax + ", position="
				+ position + "]";
	}

}
