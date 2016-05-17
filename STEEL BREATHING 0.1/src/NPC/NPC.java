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

		names = new ArrayList<>();
		generateNames();
	}

	private void generateNames() {
		names.add("aa");
		names.add("ab");
		names.add("ac");
		names.add("av");
		names.add("aw");
		names.add("ax");
		names.add("an");
		names.add("aq");
		names.add("as");
		names.add("dd");
		names.add("ad");
		names.add("af");

		names.add("aaa");
		names.add("aba");
		names.add("aca");
		names.add("ava");
		names.add("awa");
		names.add("axa");
		names.add("ana");
		names.add("aqa");
		names.add("asa");
		names.add("dda");
		names.add("ada");
		names.add("afa");

		names.add("aaab");
		names.add("abab");
		names.add("acab");
		names.add("avab");
		names.add("awab");
		names.add("axab");
		names.add("anab");
		names.add("aqab");
		names.add("asab");
		names.add("ddab");
		names.add("adab");
		names.add("afab");

		names.add("baa");
		names.add("bab");
		names.add("bac");
		names.add("bav");
		names.add("baw");
		names.add("bax");
		names.add("ban");
		names.add("baq");
		names.add("bas");
		names.add("bdd");
		names.add("bad");
		names.add("baf");

		names.add("baaa");
		names.add("baba");
		names.add("baca");
		names.add("bava");
		names.add("bawa");
		names.add("baxa");
		names.add("bana");
		names.add("baqa");
		names.add("basa");
		names.add("bdda");
		names.add("bada");
		names.add("bafa");

		names.add("baaab");
		names.add("babab");
		names.add("bacab");
		names.add("bavab");
		names.add("bawab");
		names.add("baxab");
		names.add("banab");
		names.add("baqab");
		names.add("basab");
		names.add("bddab");
		names.add("badab");
		names.add("bafab");

		names.add("aaaa");
		names.add("abaa");
		names.add("acaa");
		names.add("avaa");
		names.add("awaa");
		names.add("axaa");
		names.add("aana");
		names.add("aaqa");
		names.add("aasa");
		names.add("dada");
		names.add("adaa");
		names.add("afaa");

		names.add("aaaaa");
		names.add("abaaa");
		names.add("acaaa");
		names.add("avaaa");
		names.add("awaaa");
		names.add("axaaa");
		names.add("aaana");
		names.add("aaqaa");
		names.add("aasaa");
		names.add("dadaa");
		names.add("adaaa");
		names.add("afaaa");

		names.add("aaaaaa");
		names.add("abaaaa");
		names.add("acaaaa");
		names.add("avaaaa");
		names.add("awaaaa");
		names.add("axaaaa");
		names.add("aaaana");
		names.add("aaqaaa");
		names.add("aasaaa");
		names.add("dadaaa");
		names.add("adaaaa");
		names.add("afaaaa");

		names.add("aaaaab");
		names.add("abaaab");
		names.add("acaaab");
		names.add("avaaab");
		names.add("awaaab");
		names.add("axaaab");
		names.add("aaaaab");
		names.add("aaqaab");
		names.add("aasaab");
		names.add("dadaab");
		names.add("adaaab");
		names.add("afaaab");
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
					recieveCleanMap();
					setMapClean();
					break;

				case 103: // list of players
					ArrayList<Avatar> data = recieveOtherPlayers();
					receivedDataProcessing(data);
					break;
				default:
					break;
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@SuppressWarnings("unchecked")
	private ArrayList<Avatar> recieveOtherPlayers() {
		try {
			int length = bufferIn.getInt();

			if (bufferIn.remaining() > length)
				dataIn = new byte[length];
			else
				dataIn = new byte[bufferIn.remaining()];

			bufferIn.get(dataIn);
			bufferIn = ByteBuffer.allocate(length);
			bufferIn.put(dataIn);

			System.out.println("le total : " + length);
			System.out.println("le reste premier : " + dataIn.length);
			if (length < dataIn.length) {
				System.exit(0);
			}
			int n;
			while ((n = socket.read(bufferIn)) != 0) {
				// System.out.println("loading :  " + n);
				if (bufferIn.position() >= length)
					break;
			}
			bufferIn.rewind();
			// System.out.println(dataIn.length);
			bis = new ByteArrayInputStream(bufferIn.array());
			ois = new ObjectInputStream(bis);
			return (ArrayList<Avatar>) ois.readObject();
		} catch (IOException | ClassNotFoundException e) {
			e.printStackTrace();
		}
		return null;
	}

	private void recieveCleanMap() {
		try {
			int n;
			int length = bufferIn.getInt();
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
			mapClean = (Element[][]) ois.readObject();
		} catch (IOException | ClassNotFoundException e) {
			e.printStackTrace();
		}
	}

	private void recieveHero() {
		try {
			dataIn = new byte[bufferIn.remaining()];
			bufferIn.get(dataIn);
			// System.out.println(dataIn.length);
			bis = new ByteArrayInputStream(dataIn);
			ois = new ObjectInputStream(bis);
			hero = (Avatar) ois.readObject();
			// System.out.println(hero);
			bufferIn.clear();
		} catch (IOException | ClassNotFoundException e) {
			e.printStackTrace();
		}
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

	public void reAllocateBuffer() {
		bufferIn = ByteBuffer.allocate(5000);
	}

	public static void main(String[] args) throws IOException {
		for (int i = 0; i < 99; i++) {
			try {
				Thread.sleep(200);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			new Thread(new NPC(9090, "localhost")).start();
		}
	}
}
