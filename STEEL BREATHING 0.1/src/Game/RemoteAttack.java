package Game;

public abstract class RemoteAttack extends Element{
	protected String name;
	protected Actor actor;
	
	protected Maps map;
	protected Zone zone;

	protected Direction direction;
	protected Condition condition;
	protected State state;
	
	protected int speed;
}
