package Server;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.TreeSet;

import Exception.NameExistsException;
import Game.Action;
import Game.Avatar;
import Game.Condition;
import Game.Direction;
import Game.Element;
import Game.Ground;
import Game.Maps;
import Game.Player;
import Game.Position;
import Game.State;
import Game.Wait;
import Manager.Creator;

public class Server {
	private ServerSocketChannel serverSocket;
	public Selector selector;
	private ByteBuffer buffer;

	private Element[][] mapClean;
	private Maps map;

	private HashMap<String, SocketChannel> channelByName;
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

	}

	public void run() throws IOException {
		System.out.println("SERVER LAUNCHED..");
		long interval, time1, time2;
		while (true) {
			interval = 100;
			time1 = System.currentTimeMillis();
			while (interval > 0) {
				selector.select(interval);
				time2 = System.currentTimeMillis();
				interval -= time2 - time1;
				selector.selectedKeys().forEach(k -> {
					if (k.isValid()) {
						if (k.isAcceptable()) {
							// System.out.println("Acceptable");
						accept();
					} else if (k.isReadable()) {
						// System.out.println("Readable");
						read(k);
					} else if (k.isWritable()) {
						write(k);
					}
				}
			}	);
				selector.selectedKeys().clear();
			}
			// GAME'S LOGIC
			ProgressTheGame();
			PrepareUsersForNewsReception();
		}
	}

	private void ProgressTheGame() {
		actualizeRemoteAttacks();
	}

	private void actualizeRemoteAttacks() {
		map.getRemoteAttacks().forEach(r -> r.progress());
		map.getBlownAttacks().forEach(r -> map.getRemoteAttacks().remove(r));
		map.getBlownAttacks().clear();
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
			case HERO:
				// System.out.println("SENDING NAME ACCEPT");
				sendHeroData(channel);
				break;
			case INFO:
				sendActorsData(channel);
				break;
			case MAP:
				// System.out.println("SENDING MAP");
				sendMapTilesData(channel);
				break;
			case LOADING:
				// System.out.println("LOADING");
				loadingData(channel);
				break;
			default:
				break;
			}
			channel.register(selector, SelectionKey.OP_READ);
		} catch (IOException e) {
			disconnectPlayer(channel);
		}
	}

	private void read(SelectionKey k) {
		buffer.clear();
		SocketChannel channel = (SocketChannel) k.channel();
		try {
			int n;
			if ((n = channel.read(buffer)) == -1)
				throw new IOException();
			buffer.flip();
			int cmd;
			if (4 > n)
				cmd = 404;
			else
				cmd = buffer.getInt();
			System.out.println(cmd);
			switch (cmd) {
			case 0: // name request
				processingNameRequest(channel);
				break;

			case 1: // Act request
				processingActRequest(channel);
				break;

			case 5: // Replay request
				processingReplayRequest(channel);
				break;
			//
			// case 9: // Resend request
			// processingResendRequest(channel);
			// break;

			case 10: // Next request
				processingNextRequest(channel);
				break;
			default:
				break;
			}
		} catch (IOException | ClassNotFoundException e1) {
			disconnectPlayer(channel);
		}
		buffer.clear();
	}

	//
	// private void processingResendRequest(SocketChannel channel) {
	// System.out.println("there");
	// User user = userByChannel.get(channel);
	// user.setProgress(Progress.UNDONE);
	// user.reAllocateBuffer();
	// }

	private void processingNextRequest(SocketChannel channel)
			throws ClosedChannelException {
		User user = userByChannel.get(channel);
		switch (user.getAwaiting()) {
		case HERO:
			user.setAwaiting(Wait.MAP);
			user.setProgress(Progress.UNDONE);
			System.out.println("MAP REQUEST");
			break;

		case MAP:
			user.setAwaiting(Wait.INFO);
			user.setProgress(Progress.UNDONE);
			break;

		default:
			break;
		}
	}

	private void PrepareUsersForNewsReception() {
		channelByName.forEach((key, c) -> {
			try {
				c.register(selector, SelectionKey.OP_WRITE);
			} catch (Exception e) {
				// System.out.println(".Player might be disconnected");
				disconnectPlayer(c);
			}
		});
	}

	private void processingNameRequest(SocketChannel channel)
			throws IOException {
		try {
			String name = Charset.forName("UTF-8").decode(buffer).toString();
			if (names.contains(name) || name.length() > 10)
				throw new NameExistsException();
			Player player = new Player(name, new Position(15, 12), map, 100,
					100, 20, new State());
			player.CreateAvatar(player.getClass().getSimpleName(),
					player.getName());
			names.add(name);
			userByChannel.put(channel, new User(player));
			channelByName.put(name, channel);
			player.loadOnMap();
			// player.getZone().displayPlayers();
			// System.out.println("connected players:" + names.size());
		} catch (NameExistsException e) {
			buffer.clear();
			buffer.putInt(101);
			buffer.flip();
			channel.write(buffer);
		}
		buffer.clear();
	}

	private void processingActRequest(SocketChannel channel)
			throws IOException, ClassNotFoundException {
		User user = userByChannel.get(channel);
		ifBufferHasRemaining(buffer, channel);
		byte[] data = new byte[buffer.remaining()];
		buffer.get(data);
		ByteArrayInputStream bis = new ByteArrayInputStream(data);
		ObjectInputStream ois = new ObjectInputStream(bis);
		Action m = (Action) ois.readObject();
		System.out.println(m);
		user.getPlayer().act(m.direction, m.condition);
		buffer.clear();
	}

	private void processingReplayRequest(SocketChannel channel)
			throws IOException {
		Player player = userByChannel.get(channel).getPlayer();
		player.setLife(player.getLifeMax());
		player.setCondition(Condition.STANDING);
		player.setDirection(Direction.NONE);
		player.loadOnMap();
		player.actualizeAvatar();
		player.getState().setReason(Condition.NONE);
		// System.out.println(player.getAvatar());
	}

	private void sendHeroData(SocketChannel channel) throws IOException {
		User user = userByChannel.get(channel);
		if (user.getProgress() == Progress.UNDONE) {
			user.getBuffer().putInt(100);
			user.getBuffer().flip();
			channel.write(user.getBuffer());
			user.getBuffer().clear();

			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			ObjectOutputStream oos = new ObjectOutputStream(bos);
			oos.writeObject(user.getPlayer().getAvatar());
			byte[] data = bos.toByteArray();
			user.getBuffer().put(data);
			user.getBuffer().flip();
			channel.write(user.getBuffer());
			user.getBuffer().clear();
			user.setProgress(Progress.SENT);
		}
	}

	private void sendActorsData(SocketChannel channel) throws IOException {
		User user = userByChannel.get(channel);
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		ObjectOutputStream oos = new ObjectOutputStream(bos);
		ArrayList<Avatar> neighbors = new ArrayList<>();

		int z = user.getPlayer().getZone().getPosition().getI();
		int y = user.getPlayer().getZone().getPosition().getJ();

		for (int i = z - 1; i <= z + 1; i++) {
			for (int j = y - 1; j <= y + 1; j++) {
				neighbors.addAll(map.getZones()[i][j].getPlayers());
				neighbors.addAll(map.getZones()[i][j].getAttacks());
			}
		}
		// System.out.println(neighbors.size());
		oos.writeObject(neighbors);
		byte[] data = bos.toByteArray();
		if (data.length < user.getSize()) {
			user.reAllocateBuffer();
			user.getBuffer().putInt(103);
			user.getBuffer().putInt(data.length);
			user.getBuffer().flip();
			channel.write(user.getBuffer());
			channel.write(ByteBuffer.wrap(data));
		} else
			prepareBigDataForTransfer(data, channel, 103);
	}

	private void sendMapTilesData(SocketChannel channel) throws IOException {
		User user = userByChannel.get(channel);
		if (user.getProgress() == Progress.UNDONE) {
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			ObjectOutputStream oos = new ObjectOutputStream(bos);
			oos.writeObject(mapClean);

			byte[] data = bos.toByteArray();
			System.out.println("Map size" + data.length);
			prepareBigDataForTransfer(data, channel, 102);
			user.setProgress(Progress.SENT);
		}
	}

	private void prepareBigDataForTransfer(byte[] data, SocketChannel channel,
			int type) throws IOException {
		User user = userByChannel.get(channel);
		user.getBuffer().clear();
		// System.out.println("Data : " + data.length);
		user.getBuffer().putInt(type);
		user.getBuffer().putInt(data.length);
		user.getBuffer().flip();
		channel.write(user.getBuffer());
		user.getBuffer().clear();
		user.setBuffer(ByteBuffer.wrap(data));
		// System.out.println("buffer : "
		// + userByChannel.get(channel).getBuffer().capacity());
		user.setAwaiting(Wait.LOADING);
	}

	private void loadingData(SocketChannel channel) throws IOException {
		User user = userByChannel.get(channel);
		int position = 0;
		byte[] data;
		while (user.getBuffer().hasRemaining()) {
			position = user.getBuffer().position();

			if (user.getBuffer().remaining() >= user.getSize())
				data = new byte[user.getSize()];
			else
				data = new byte[user.getBuffer().remaining()];

			user.getBuffer().get(data);
			// System.out.println("data : " + data.length);
			int n = channel.write(ByteBuffer.wrap(data));
			// System.out.println("data sent : " + n);
			if (n == 0) {
				user.getBuffer().position(position);
				channel.register(selector, SelectionKey.OP_WRITE);
				break;
			}
		}
		if (user.getBuffer().position() == user.getBuffer().capacity()) {
			user.reAllocateBuffer();
			user.setAwaiting(Wait.INFO);
			channel.register(selector, SelectionKey.OP_READ);
		}
	}

	private void disconnectPlayer(SocketChannel channel) {
		try {
			if (userByChannel.containsKey(channel)) {
				User user = userByChannel.get(channel);
				System.out.println(user.getPlayer().getName()
						+ " : is disconnected");
				map.getTiles()[user.getPlayer().getPosition().getI()][user
						.getPlayer().getPosition().getJ()] = new Ground();
				user.getPlayer().getZone().getPlayers()
						.remove(user.getPlayer().getAvatar());
				channelByName.remove(user.getPlayer().getName());
				names.remove(user.getPlayer().getName());
				userByChannel.remove(channel);
			}
			channel.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void ifBufferHasRemaining(ByteBuffer buffer, SocketChannel channel)
			throws IOException {
		if (!buffer.hasRemaining()) {
			buffer.clear();
			channel.read(buffer);
			buffer.flip();
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
