package Local;

public class Displayer implements Runnable {
	SBLocal local;
	int speed;

	public Displayer(SBLocal local, int speed) {
		this.local = local;
		this.speed = speed;
	}

	@Override
	public void run() {
		while (true) {
			try {
				Thread.sleep(speed);
				local.repaint();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

}
