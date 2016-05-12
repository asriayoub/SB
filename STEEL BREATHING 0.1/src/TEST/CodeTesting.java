package TEST;

import java.io.IOException;

import Game.Maps;

public class CodeTesting {

	public static void main(String[] args) throws IOException {

		long firstInstant = System.currentTimeMillis();
		for (int i = 0; i < 10000; i++) {
			System.out.println(new Maps());
		}
		long secondeInstant = System.currentTimeMillis();
		System.out.println(secondeInstant-firstInstant);
	}

}
