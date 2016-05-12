package Server;

import java.nio.channels.SelectionKey;

public class Worker implements Runnable {
	Server server;

	public Worker(Server server) {
		this.server = server;
	}


	@Override
	public void run() {
		while (true) {
			try {
				Thread.sleep(100);
			} catch (InterruptedException e1) {
				e1.printStackTrace();
			}
			server.channelByName.forEach((key, c) -> {
				try {
					c.register(server.selector, SelectionKey.OP_WRITE);
				} catch (Exception e) {
					System.out.println(".Player might be disconnected");
					e.printStackTrace();
				}
			});
		}
	}

}
