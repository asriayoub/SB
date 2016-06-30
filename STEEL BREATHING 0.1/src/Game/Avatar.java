package Game;

import java.io.Serializable;

public class Avatar implements Serializable {
	private static final long serialVersionUID = 1L;

	public Kind kind;
	public String name;

	public Position position;
	
	public Direction direction;
	public Condition condition;

	public int level;

	public int life;
	public int lifeMax;

	public Avatar() {

	}

	public Avatar(Kind kind, String name, Position position,
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

	public Avatar(Kind kind, String name) {
		this.kind = kind;
		this.name = name;
	}

	
	public Kind getKind() {
		return kind;
	}

	public void setKind(Kind kind) {
		this.kind = kind;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Position getPosition() {
		return position;
	}

	public void setPosition(Position position) {
		this.position = position;
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

	public int getLevel() {
		return level;
	}

	public void setLevel(int level) {
		this.level = level;
	}

	public int getLife() {
		return life;
	}

	public void setLife(int life) {
		this.life = life;
	}

	public int getLifeMax() {
		return lifeMax;
	}

	public void setLifeMax(int lifeMax) {
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
