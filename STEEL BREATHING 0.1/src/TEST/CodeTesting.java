package TEST;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.TreeSet;

import Game.Maps;

public class CodeTesting {

	public static ArrayList<String> generateAlphabetNames(int length, int number) {
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

	public static void main(String[] args) throws IOException {
		ArrayList<String> names=generateAlphabetNames(10, 100);
		names.forEach(n->System.out.println(n));
	}

}
