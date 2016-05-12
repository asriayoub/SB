package Local;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.IOException;

import javax.swing.JFrame;

import Game.Condition;
import Game.Direction;
import Game.Element;
import Game.Gap;
import Game.Ground;
import Game.Maps;
import Game.Obstacle;
import Game.Player;
import Game.Position;
import Game.State;
import Manager.Creator;

public class SBLocal extends JFrame implements KeyListener, MouseListener {
	private static final long serialVersionUID = 1L;
	int pixel = 30;
	int height = 19;
	int width = 19;

	int viewHeight = pixel * height;
	int viewWidth = pixel * width;
	int keyValue;

	Position center;

	Maps map;

	Player player;
	Condition condition;
	Direction direction;

	public SBLocal(Maps m, Player p) {
		map = m;
		player = p;
		p.loadOnMap(m);

		System.out.println(player.getName() + player.getPosition());
		System.out.println("Zone : " + player.getZone().getPosition());

		center = new Position(width / 2, height / 2);

		setSize(viewWidth, viewHeight);
		setTitle("Test");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setResizable(false);
		setLocationRelativeTo(null);
		setVisible(true);
		addKeyListener(this);
		addMouseListener(this);

		condition = Condition.STANDING;
		direction = Direction.NONE;
		keyValue = 0;

		new Thread(new Displayer(this, 100)).start();
	}

	@Override
	public void paint(Graphics g) {
		int fromI = player.getPosition().getI() - height / 2;
		int toI = player.getPosition().getI() + height / 2;

		int fromJ = player.getPosition().getJ() - width / 2;
		int toJ = player.getPosition().getJ() + width / 2;

		for (int i = fromI; i < toI + 1; i++) {
			for (int j = fromJ; j < toJ + 1; j++) {
				g.setColor(getColorElement(map.getTiles()[i][j]));
				g.fillRect((j - fromJ) * pixel, (i - fromI) * pixel, pixel,
						pixel);
			}
		}
	}

	public Color getColorElement(Element e) {
		if (e instanceof Ground)
			return Color.WHITE;
		if (e instanceof Gap)
			return Color.GRAY;
		if (e instanceof Obstacle)
			return Color.BLACK;
		if (e instanceof Player)
			return Color.RED;
		return null;
	}

	@Override
	public void keyPressed(KeyEvent k) {
		int code = k.getKeyCode();
		if (condition == Condition.STANDING)
			condition = Condition.WALKING;
		switch (code) {
		case KeyEvent.VK_W:
			if (keyValue == 0)
				condition = Condition.STRIKING;
			else
				condition = Condition.STANDING;
			keyValue++;
			break;
		case KeyEvent.VK_X:
			condition = Condition.RUNNING;
			System.out.println("RUNNING");
			break;
		case KeyEvent.VK_C:
			condition = Condition.JUMPING;
			System.out.println("JUMPING");
			break;
		case KeyEvent.VK_UP:
			direction = Direction.UP;
			player.act(direction, condition);
			break;
		case KeyEvent.VK_DOWN:
			direction = Direction.DOWN;
			player.act(direction, condition);
			break;
		case KeyEvent.VK_RIGHT:
			direction = Direction.RIGHT;
			player.act(direction, condition);
			break;
		case KeyEvent.VK_LEFT:
			direction = Direction.LEFT;
			player.act(direction, condition);
			break;
		default:
			break;
		}
		System.out.println(condition);
	}

	@Override
	public void keyReleased(KeyEvent k) {
		int code = k.getKeyCode();
		switch (code) {
		case KeyEvent.VK_W:
			keyValue = 0;
			break;
		case KeyEvent.VK_X:
			condition = Condition.STANDING;
			break;
		case KeyEvent.VK_C:
			condition = Condition.WALKING;
			break;
		case KeyEvent.VK_UP:
			if (!(condition == Condition.RUNNING))
				condition = Condition.STANDING;
			break;
		case KeyEvent.VK_DOWN:
			if (!(condition == Condition.RUNNING))
				condition = Condition.STANDING;
			break;
		case KeyEvent.VK_RIGHT:
			if (!(condition == Condition.RUNNING))
				condition = Condition.STANDING;
			break;
		case KeyEvent.VK_LEFT:
			if (!(condition == Condition.RUNNING))
				condition = Condition.STANDING;
			break;
		default:
			keyValue = 0;
			break;
		}

	}

	@Override
	public void keyTyped(KeyEvent k) {
		int code = k.getKeyCode();
		switch (code) {
		default:
			break;
		}
	}

	@Override
	public void mouseClicked(MouseEvent m) {

	}

	@Override
	public void mouseEntered(MouseEvent m) {

	}

	@Override
	public void mouseExited(MouseEvent m) {
	}

	@Override
	public void mousePressed(MouseEvent m) {
		int intervalI = player.getPosition().getI() - center.getJ();
		int intervalJ = player.getPosition().getJ() - center.getI();

		int i = 0;
		int j = 0;

		j = m.getX() / pixel;
		i = m.getY() / pixel;

		System.out.println("MOUSE TRUE VALUE : " + new Position(i, j));
		System.out.println("MOUSE MAPS VALUE : "
				+ new Position(intervalI + i, intervalJ + j));
		System.out.println(map.getTiles()[intervalI + i][intervalJ + j]);
	}

	@Override
	public void mouseReleased(MouseEvent m) {
	}

	public static void main(String[] args) {
		try {
			Creator creator = new Creator();
			Maps m = creator.readMapFromFile("BattleField");
			creator.displayZones(m);

			Player p = new Player("Hero", new Position(15, 12), m, 100, 100, 20, new State());
			p.setDirection(Direction.NONE);
			p.setCondition(Condition.STANDING);

			Player t = new Player("enemy", new Position(30, 25), m, 100, 100,
					20, new State());
			t.loadOnMap(m);
			p.setDirection(Direction.NONE);
			p.setCondition(Condition.STANDING);

			new SBLocal(m, p);

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
