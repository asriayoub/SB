package Client;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

import javax.swing.JFrame;

import org.apache.commons.lang.SerializationUtils;

import Game.Condition;
import Game.Direction;
import Game.Element;
import Game.Gap;
import Game.Ground;
import Game.Obstacle;
import Game.Player;
import Game.Position;

public class Client extends JFrame implements Runnable, KeyListener,
		MouseListener {
	private static final long serialVersionUID = 1L;

	SocketChannel socket;

	ByteBuffer bufferIn;
	ByteBuffer bufferOut;
	byte[] dataIn;
	byte[] dataOut;
	ByteArrayInputStream bis;
	ObjectInputStream ois;

	Element[][] mapClean;
	Element[][] map;
	Avatar hero;
	HashMap<Position, Player> otherPlayers;

	Condition condition;
	Direction direction;
	int keyTyped;

	Position center;
	int pixel = 30;
	int height = 19;
	int width = 19;
	int viewHeight = pixel * height;
	int viewWidth = pixel * width;

	public Client(int port, String adress) throws IOException {
		bufferIn = ByteBuffer.allocate(5000);
		bufferOut = ByteBuffer.allocate(1028);
		bis = null;
		ois = null;

		socket = SocketChannel.open();
		socket.configureBlocking(true);
		socket.socket().connect(new InetSocketAddress(port));

		otherPlayers = new HashMap<>();

		center = new Position(width / 2, height / 2);

		setSize(viewWidth, viewHeight);
		setTitle("Test");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setResizable(false);
		setLocationRelativeTo(null);
		setVisible(false);
		addKeyListener(this);
		addMouseListener(this);

		condition = Condition.STANDING;
		direction = Direction.NONE;
		keyTyped = 0;

		hero = new Avatar();
		mapClean = null;
		map = null;
	}

	@Override
	public void run() {
		try {
			sendName();
			while (true) {
				reAllocateBufferIn();
				if (socket.read(bufferIn) == -1)
					throw new IOException();
				bufferIn.flip();
				int response = bufferIn.getInt();
				System.out.println(bufferIn.capacity());
				// System.out.println(response);
				switch (response) {

				case 100: // hero
					recieveHero();
					break;

				case 101: // name Incorrect
					sendName();
					break;

				case 102: // map
					recieveCleanMap();
					setMapClean();
					setVisible(true);
					break;

				case 103: // list of players
					ArrayList<Avatar> data = recieveOtherPlayers();
					receivedDataProcessing(data);
					break;
				default:
					break;
				}
			}
		} catch (IOException | ClassNotFoundException e) {
			e.printStackTrace();
		}
	}

	@SuppressWarnings("unchecked")
	private ArrayList<Avatar> recieveOtherPlayers()
			throws ClassNotFoundException, IOException {
		ifBufferHasRemaining();
		collectBigData();
		return (ArrayList<Avatar>) ois.readObject();
	}

	private void recieveCleanMap() throws IOException, ClassNotFoundException {
		ifBufferHasRemaining();
		collectBigData();
		mapClean = (Element[][]) ois.readObject();
	}

	private void recieveHero() throws IOException, ClassNotFoundException {
		ifBufferHasRemaining();
		dataIn = new byte[bufferIn.remaining()];
		bufferIn.get(dataIn);
		// System.out.println(dataIn.length);
		bis = new ByteArrayInputStream(dataIn);
		ois = new ObjectInputStream(bis);
		hero = (Avatar) ois.readObject();
		System.out.println(hero);
		bufferIn.clear();
	}

	public void receivedDataProcessing(ArrayList<Avatar> list) {
		setMapClean();
		System.out.println("List length :" + list.size());
		// list.forEach(r -> System.out.println(r));
		list.forEach(actor -> {
			if (actor.name.equals(hero.name))
				hero = actor;
			map[actor.i][actor.j] = actor;
		});
		System.out.println("[" + hero.i + "][" + hero.j + "]");
		repaint();
	}

	public void setMapClean() {
		map = new Element[mapClean.length][mapClean.length];
		for (int i = 0; i < mapClean.length; i++)
			for (int j = 0; j < mapClean.length; j++)
				map[i][j] = mapClean[i][j];
	}

	public void sendName() {
		try {
			bufferOut.clear();
			Scanner sc = new Scanner(System.in);
			System.out.println("Insert your name :");
			String name = sc.nextLine();
			bufferOut.putInt(0);
			dataOut = name.getBytes();
			bufferOut.put(dataOut);
			bufferOut.flip();
			socket.write(bufferOut);
			sc.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void SendMove() {
		try {
			bufferOut.putInt(1);
			dataOut = SerializationUtils.serialize(new Action(direction,
					condition));
			bufferOut.put(dataOut);
			bufferOut.flip();
			socket.write(bufferOut);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void collectBigData() throws IOException {
		int n;
		int length = bufferIn.getInt();
		if (bufferIn.remaining() > length)
			dataIn = new byte[length];
		else
			dataIn = new byte[bufferIn.remaining()];
		bufferIn.get(dataIn);
		bufferIn = ByteBuffer.allocate(length);
		bufferIn.put(dataIn);
		while ((n = socket.read(bufferIn)) != 0) {
			// System.out.println("loading :  " + n);
			if (bufferIn.position() >= length)
				break;
		}
		bufferIn.rewind();
		bis = new ByteArrayInputStream(bufferIn.array());
		ois = new ObjectInputStream(bis);
	}

	public void reAllocateBufferIn() {
		bufferIn = ByteBuffer.allocate(5000);
	}

	private void ifBufferHasRemaining() throws IOException {
		if (!bufferIn.hasRemaining()) {
			bufferIn.clear();
			socket.read(bufferIn);
			bufferIn.flip();
		}
	}

	@Override
	public void paint(Graphics g) {
		int fromI = hero.i - height / 2;
		int toI = hero.i + height / 2;

		int fromJ = hero.j - width / 2;
		int toJ = hero.j + width / 2;

		for (int i = fromI; i < toI + 1; i++) {
			for (int j = fromJ; j < toJ + 1; j++) {
				g.setColor(getColorElement(map[i][j]));
				g.fillRect((j - fromJ) * pixel, (i - fromI) * pixel, pixel,
						pixel);
			}
		}
	}

	public Color getColorElement(Element e) {
		if (e instanceof Ground)
			return Color.WHITE;
		if (e instanceof Gap)
			return Color.BLACK;
		if (e instanceof Obstacle)
			return Color.BLACK;
		if (e instanceof Avatar && e == hero)
			return Color.RED;
		if (e instanceof Avatar && e != hero) {
			Avatar a = (Avatar) e;
			if (a.condition.equals("DEAD"))
				return Color.GRAY;
			else
				return Color.BLUE;
		}
		return null;
	}

	@Override
	public void keyPressed(KeyEvent k) {
		bufferOut.clear();
		int code = k.getKeyCode();
		System.out.println(hero.condition);
		if (condition == Condition.STANDING)
			condition = Condition.WALKING;
		switch (code) {
		case KeyEvent.VK_W:
			if (keyTyped == 0) {
				condition = Condition.STRIKING;
				SendMove();
			} else
				condition = Condition.STANDING;
			keyTyped++;
			break;
		case KeyEvent.VK_X:
			condition = Condition.RUNNING;
			break;
		case KeyEvent.VK_C:
			condition = Condition.JUMPING;
			break;
		case KeyEvent.VK_UP:
			direction = Direction.UP;
			SendMove();
			break;
		case KeyEvent.VK_DOWN:
			direction = Direction.DOWN;
			SendMove();
			break;
		case KeyEvent.VK_RIGHT:
			direction = Direction.RIGHT;
			SendMove();
			break;
		case KeyEvent.VK_LEFT:
			direction = Direction.LEFT;
			SendMove();
			break;
		default:
			break;
		}
	}

	@Override
	public void keyReleased(KeyEvent k) {
		int code = k.getKeyCode();
		switch (code) {
		case KeyEvent.VK_W:
			keyTyped = 0;
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
			break;
		}

	}

	@Override
	public void keyTyped(KeyEvent k) {
		// TODO Auto-generated method stub

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
	public void mousePressed(MouseEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseReleased(MouseEvent arg0) {
		// TODO Auto-generated method stub

	}

	public static void main(String[] args) throws IOException {
		new Thread(new Client(9090, "localhost")).start();
	}
}
