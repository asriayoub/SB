package NPC;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import org.apache.commons.lang.SerializationUtils;

import Client.Action;
import Client.Avatar;
import Game.Condition;
import Game.Direction;
import Game.Element;
import Game.Player;
import Game.Position;

public class NPC implements Runnable {
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

	List<String> names;

	public NPC(int port, String adress) throws IOException {
		bufferIn = ByteBuffer.allocate(5000);
		bufferOut = ByteBuffer.allocate(5000);
		bis = null;
		ois = null;

		socket = SocketChannel.open();
		socket.configureBlocking(true);
		socket.socket().connect(new InetSocketAddress(port));

		otherPlayers = new HashMap<>();

		condition = Condition.STANDING;
		direction = Direction.NONE;
		keyTyped = 0;

		hero = new Avatar();
		mapClean = null;
		map = null;

		names = generateAlphabetNames(10, 200);
	}

	public ArrayList<String> generateAlphabetNames(int length, int number) {
		ArrayList<String> names = new ArrayList<>();
		char[] alphabet = { 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j',
				'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v',
				'w', 'x', 'y', 'z' };

		Random rand = new Random();
		String name;
		int r;
		for (int j = 0; j < number; j++) {
			name = "";
			for (int i = 0; i < length; i++) {
				r = rand.nextInt(26);
				name += alphabet[r];
			}
			names.add(name);
		}
		return names;
	}

	@Override
	public void run() {
		try {
			sendName();
			while (true) {
				reAllocateBuffer();
				if (socket.read(bufferIn) == -1)
					throw new IOException();
				bufferIn.flip();
				int response = bufferIn.getInt();
				// System.out.println(response);
				switch (response) {

				case 100: // hero
					recieveHero();
					new Thread(new Spamer(this)).start();
					break;

				case 101: // name Incorrect
					sendName();
					break;

				case 102: // map
					System.out.println(Thread.currentThread().getName() + " : "
							+ response);
					recieveCleanMap();
					setMapClean();
					break;

				case 103: // list of players
					ArrayList<Avatar> data = recieveOtherPlayers();
					if (map != null)
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
	private ArrayList<Avatar> recieveOtherPlayers() throws IOException,
			ClassNotFoundException {
		ifBufferHasRemaining();
		collectBigData();
		return (ArrayList<Avatar>) ois.readObject();
	}

	private void recieveCleanMap() throws ClassNotFoundException, IOException {
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
		// System.out.println(hero);
		bufferIn.clear();
	}

	public void receivedDataProcessing(ArrayList<Avatar> list) {
		setMapClean();
		// System.out.println("List length :" + list.size());
		// list.forEach(r -> System.out.println(r));
		list.forEach(actor -> {
			if (actor.name.equals(hero.name))
				hero = actor;
			map[actor.i][actor.j] = actor;
		});
		// System.out.println("[" + hero.i + "][" + hero.j + "]");
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
			bufferOut.putInt(0);
			dataOut = names.get(0).getBytes();
			names.remove(0);
			bufferOut.put(dataOut);
			bufferOut.flip();
			socket.write(bufferOut);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void SendMove() {
		try {
			bufferOut.clear();
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

	public void reAllocateBuffer() {
		bufferIn = ByteBuffer.allocate(5000);
	}

	private void ifBufferHasRemaining() throws IOException {
		if (!bufferIn.hasRemaining()) {
			bufferIn.clear();
			socket.read(bufferIn);
			bufferIn.flip();
		}
	}

	public static void main(String[] args) {
		for (int i = 0; i < 190; i++) {
			try {
				Thread.sleep(100);
				new Thread(new NPC(9090, "localhost")).start();
			} catch (InterruptedException | IOException e) {
				System.exit(0);
			}
		}
	}
}
