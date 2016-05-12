package Server;

import java.nio.ByteBuffer;

import Game.Player;

public class User {
	private ByteBuffer buffer;
	private int size;

	private String awaiting;

	private Player player;

	public User(Player player) {
		this.player = player;
		buffer = ByteBuffer.allocate(5000);
		size = 5000;
		awaiting = "HERO";
	}

	public ByteBuffer getBuffer() {
		return buffer;
	}

	public void setBuffer(ByteBuffer buffer) {
		this.buffer = buffer;
	}

	public int getSize() {
		return size;
	}

	public void setSize(int size) {
		this.size = size;
	}

	public String getAwaiting() {
		return awaiting;
	}

	public void setAwaiting(String awaiting) {
		this.awaiting = awaiting;
	}

	public Player getPlayer() {
		return player;
	}

	public void setPlayer(Player player) {
		this.player = player;
	}

	public void reAllocateBuffer() {
		buffer = ByteBuffer.allocate(size);
	}

	
}
