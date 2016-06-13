package Server;

import java.nio.ByteBuffer;

import Game.Player;
import Game.Wait;

public class User {
	private ByteBuffer buffer;
	private int size;

	private Wait awaiting;
	private Progress progress;

	private Player player;

	public User(Player player) {
		this.player = player;
		buffer = ByteBuffer.allocate(5000);
		size = 5000;
		awaiting = Wait.HERO;
		progress = Progress.UNDONE;
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

	public Wait getAwaiting() {
		return awaiting;
	}

	public void setAwaiting(Wait awaiting) {
		this.awaiting = awaiting;
	}

	public Player getPlayer() {
		return player;
	}

	public void setPlayer(Player player) {
		this.player = player;
	}

	public Progress getProgress() {
		return progress;
	}

	public void setProgress(Progress progress) {
		this.progress = progress;
	}

	public void reAllocateBuffer() {
		buffer = ByteBuffer.allocate(size);
	}

}
