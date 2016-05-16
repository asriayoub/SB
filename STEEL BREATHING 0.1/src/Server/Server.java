package Server;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.TreeSet;

import org.apache.commons.lang.SerializationUtils;

import Client.Action;
import Client.Avatar;
import Exception.NameExistsException;
import Game.Condition;
import Game.Element;
import Game.Maps;
import Game.Player;
import Game.Position;
import Game.State;
import Manager.Creator;

public class Server {
	private ServerSocketChannel serverSocket;
	public Selector selector;
	private ByteBuffer buffer;

	private Element[][] mapClean;
	private Maps map;

	public HashMap<String, SocketChannel> channelByName;
	private HashMap<SocketChannel, User> userByChannel;
	private Collection<String> names;

	public Server(int port) throws IOException {
		buffer = ByteBuffer.allocate(1024);

		selector = Selector.open();

		serverSocket = ServerSocketChannel.open();
		serverSocket.configureBlocking(false);
		serverSocket.socket().bind(new InetSocketAddress(port));
		serverSocket.register(selector, SelectionKey.OP_ACCEPT);

		channelByName = new HashMap<>();
		userByChannel = new HashMap<>();
		names = new TreeSet<>();

		Creator creator = new Creator();
		mapClean = creator.readMapFromFile("BattleField").getTiles();
		map = creator.readMapFromFile("BattleField");
		creator.displayZones(map);

		new Thread(new Worker(this)).start();
	}

	public void run() throws IOException {
		System.out.println("SERVER LAUNCHED..");
		while (true) {
			selector.select(100);
			selector.selectedKeys().forEach(k -> {
				if (k.isValid()) {
					if (k.isAcceptable()) {
						System.out.println("Acceptable");
						accept();
					} else if (k.isReadable()) {
						System.out.println("Readable");
						read(k);
					} else if (k.isWritable()) {
						write(k);
					}
				}
			});
			selector.selectedKeys().clear();
		}
	}

	private void accept() {
		try {
			System.out.println(".New Client is Connected");
			SocketChannel channel = serverSocket.accept();
			channel.configureBlocking(false);
			channel.register(selector, SelectionKey.OP_READ);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void write(SelectionKey k) {
		SocketChannel channel = (SocketChannel) k.channel();
		try {
			switch (userByChannel.get(channel).getAwaiting()) {
			case "HERO":
				System.out.println("SENDING NAME ACCEPT");
				sendacceptName(channel);
				break;
			case "INFO":
				sendActorsData(channel);
				break;
			case "MAP":
				System.out.println("SENDING MAP");
				sendMapTilesData(channel);
				break;
			case "LOADING":
				System.out.println("LOADING");
				loadingData(channel);
				break;
			default:
				break;
			}
		} catch (IOException e) {
			disconnectPlayer(channel);
		}
	}

	private void read(SelectionKey k) {
		SocketChannel channel = (SocketChannel) k.channel();
		buffer.clear();
		try {
			if (channel.read(buffer) == -1)
				throw new IOException();
			buffer.flip();
			int cmd = buffer.getInt();

			switch (cmd) {
			case 0: // name request
				processingNameRequest(channel);
				break;

			case 1: // Act request
				processingActRequest(channel);
				break;

			default:
				break;
			}
		} catch (IOException e1) {
			disconnectPlayer(channel);
		}
	}

	private void processingNameRequest(SocketChannel channel)
			throws IOException {
		try {
			String name = Charset.forName("UTF-8").decode(buffer).toString();
			if (names.contains(name) || name.length() > 10)
				throw new NameExistsException();
			Player player = new Player(name, new Position(15, 12), map, 100,
					100, 20, new State());
			names.add(name);
			userByChannel.put(channel, new User(player));
			channelByName.put(name, channel);
			player.loadOnMap(map);
			player.getZone().displayPlayers();

			System.out.println("connected players:" + names.size());
		} catch (NameExistsException e) {
			buffer.clear();
			buffer.putInt(101);
			buffer.flip();
			channel.write(buffer);
		}
	}

	private void processingActRequest(SocketChannel channel) {
		byte[] data = new byte[buffer.remaining()];
		buffer.get(data);
		ByteArrayInputStream bis = new ByteArrayInputStream(data);
		Action m = (Action) SerializationUtils.deserialize(bis);
		System.out.println(m);
		userByChannel.get(channel).getPlayer().act(m.direction, m.condition);
	}

	private void sendacceptName(SocketChannel channel) throws IOException {
		userByChannel.get(channel).getBuffer().putInt(100);
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		ObjectOutputStream oos = new ObjectOutputStream(bos);
		oos.writeObject(userByChannel.get(channel).getPlayer().getZone()
				.getPlayers()
				.get(userByChannel.get(channel).getPlayer().getName()));
		byte[] data = bos.toByteArray();
		userByChannel.get(channel).getBuffer().put(data);
		userByChannel.get(channel).getBuffer().flip();
		channel.write(userByChannel.get(channel).getBuffer());
		userByChannel.get(channel).getBuffer().clear();

		userByChannel.get(channel).setAwaiting("MAP");
	}

	private void sendActorsData(SocketChannel channel) throws IOException {
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		ObjectOutputStream oos = new ObjectOutputStream(bos);

		int z = userByChannel.get(channel).getPlayer().getZone().getPosition()
				.getI();
		int y = userByChannel.get(channel).getPlayer().getZone().getPosition()
				.getJ();

		ArrayList<Avatar> neighbors = new ArrayList<>();

		for (int i = z - 1; i < z + 1; i++)
			for (int j = y - 1; j < y + 1; j++)
				neighbors.addAll(map.getZones()[i][j].getPlayers().values());

		System.out.println(neighbors.size());
		ByteBuffer b = ByteBuffer.allocate(1000);
		b.putInt(103);
		oos.writeObject(neighbors);
		byte[] data = bos.toByteArray();
		System.out.println(data.length);
		b.put(data);
		b.flip();
		channel.write(b);
		channel.register(selector, SelectionKey.OP_READ);
	}

	private void sendMapTilesData(SocketChannel channel) throws IOException {
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		ObjectOutputStream oos = new ObjectOutputStream(bos);
		oos.writeObject(mapClean);
		byte[] data = bos.toByteArray();
		System.out.println("Data : " + data.length);
		userByChannel.get(channel).getBuffer().putInt(102);
		userByChannel.get(channel).getBuffer().putInt(data.length);
		userByChannel.get(channel).getBuffer().flip();
		channel.write(userByChannel.get(channel).getBuffer());
		userByChannel.get(channel).getBuffer().clear();
		userByChannel.get(channel).setBuffer(ByteBuffer.wrap(data));
		System.out.println("buffer : "
				+ userByChannel.get(channel).getBuffer().capacity());
		userByChannel.get(channel).setAwaiting("LOADING");
	}

	private void loadingData(SocketChannel channel) throws IOException {
		int position = 0;
		byte[] data;
		while (userByChannel.get(channel).getBuffer().hasRemaining()) {
			position = userByChannel.get(channel).getBuffer().position();

			if (userByChannel.get(channel).getBuffer().remaining() >= userByChannel
					.get(channel).getSize())
				data = new byte[userByChannel.get(channel).getSize()];
			else
				data = new byte[userByChannel.get(channel).getBuffer()
						.remaining()];

			userByChannel.get(channel).getBuffer().get(data);
			System.out.println("data : " + data.length);
			int n = channel.write(ByteBuffer.wrap(data));
			System.out.println("data sent : " + n);
			if (n == 0) {
				userByChannel.get(channel).getBuffer().position(position);
				break;
			}
		}
		if (userByChannel.get(channel).getBuffer().position() == userByChannel
				.get(channel).getBuffer().capacity()) {
			userByChannel.get(channel).reAllocateBuffer();
			userByChannel.get(channel).setAwaiting("INFO");
		}
	}

	private void disconnectPlayer(SocketChannel channel) {
		if (userByChannel.containsKey(channel)) {
			System.out.println(userByChannel.get(channel).getPlayer().getName()
					+ " : is disconnected");
			userByChannel.get(channel).getPlayer().getZone().getPlayers()
					.remove(userByChannel.get(channel).getPlayer().getName());
			channelByName.remove(userByChannel.get(channel).getPlayer()
					.getName());
			names.remove(userByChannel.get(channel).getPlayer().getName());
			userByChannel.remove(channel);

		}
		try {
			channel.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public String fromConditionToString(Condition e) {
		switch (e) {
		case STANDING:
			return "STANDING";
		case RUNNING:
			return "RUNNING";
		case WALKING:
			return "WALKING";
		case JUMPING:
			return "JUMPING";
		case UNREADY:
			return "UNREADY";
		case FIRING:
			return "FIRING";
		case STRIKING:
			return "STRIKING";
		case DEAD:
			return "DEAD";
		case HIT:
			return "HIT";
		default:
			return "";
		}
	}

	public static void main(String[] args) {
		try {
			new Server(9090).run();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
