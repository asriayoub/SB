package Manager;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import Game.Condition;
import Game.Direction;
import Game.Element;
import Game.Gap;
import Game.Ground;
import Game.Maps;
import Game.Obstacle;
import Game.Position;
import Game.Zone;

public class Creator {
	public void createMapTiles(int tiles, Maps map) {
		map.setLength(tiles);
		map.setTiles(new Element[tiles][tiles]);

		for (int i = 0; i < tiles; i++) {
			for (int j = 0; j < tiles; j++) {
				map.getTiles()[i][j] = new Ground();
			}
		}
	}

	public void loadZonesOnMap(Maps m, int zones) {
		int zoneLength = m.getLength() / zones;
		m.setZones(new Zone[zones][zones]);
		m.setZoneLength(zoneLength);
		int k = 0;
		int r = 0;
		int z = 0;
		for (int i = r; i < zones; i++) {
			for (int j = z; j < zones; j++) {
				m.getZones()[i][j] = new Zone("Zone " + k, new Position(i, j));
				z += zoneLength;
				k++;
			}
			r += zoneLength;
			z = 0;
		}
	}

	public void drawBorderOnMap(Maps m) {
		for (int i = 0; i < m.getLength(); i++) {
			for (int j = 0; j < m.getLength(); j++) {
				if (i < m.getZoneLength() || j < m.getZoneLength()) {
					m.getTiles()[i][j] = new Obstacle();
				}
				if (m.getLength() - m.getZoneLength() <= i
						|| m.getLength() - m.getZoneLength() <= j) {
					m.getTiles()[i][j] = new Obstacle();
				}
			}
		}
	}

	public void displayZones(Maps m) {
		System.out.println("map length :" + m.getLength());
		System.out.println("zone length :" + m.getZoneLength());
		System.out.println("zones : " + m.getLength() / m.getZoneLength());
		for (int i = 0; i < m.getZones().length; i++) {
			for (int j = 0; j < m.getZones().length; j++) {
				System.out.print(m.getZones()[i][j].getName()
						+ m.getZones()[i][j].getPosition() + " ");
			}
			System.out.println();
		}
	}

	public void displayZoneTiles(Maps m, Position p) {
		int zonelength = m.getZoneLength();// 5
		for (int i = p.getI() * zonelength; i < p.getI() * zonelength
				+ zonelength; i++) {
			for (int j = p.getJ() * zonelength; j < p.getJ() * zonelength
					+ zonelength; j++) {
				System.out.print(m.getTiles()[i][j] + " ");
			}
			System.out.println();
		}
	}

	public void writeMapToFile(Maps map, String name) throws IOException {
		FileOutputStream fos = new FileOutputStream("Maps/" + name + ".txt");
		fos.write(Integer.toString(map.getLength()).getBytes());
		fos.write(System.lineSeparator().getBytes());
		fos.write(Integer.toString(map.getZoneLength()).getBytes());
		for (int i = 0; i < map.getLength(); i++) {
			fos.write(System.lineSeparator().getBytes());
			for (int j = 0; j < map.getLength(); j++) {
				if (map.getTiles()[i][j] instanceof Ground)
					fos.write(" a".getBytes());
				if (map.getTiles()[i][j] instanceof Obstacle)
					fos.write(" b".getBytes());
				if (map.getTiles()[i][j] instanceof Gap)
					fos.write(" c".getBytes());
			}
		}
		fos.close();
	}

	public Maps readMapFromFile(String name) throws IOException {
		Maps m = new Maps();
		String lines = "";
		String line = "";
		int z = 0;
		BufferedReader br = new BufferedReader(new InputStreamReader(
				new FileInputStream("Maps/" + name + ".txt")));
		m.setLength(new Integer(br.readLine()));
		m.setTiles(new Element[m.getLength()][m.getLength()]);
		m.setZoneLength(new Integer(br.readLine()));
		while ((line = br.readLine()) != null) {
			lines += line;
		}
		for (int i = 0; i < m.getLength(); i++) {
			for (int j = 0; j < m.getLength(); j++) {
				++z;
				if (lines.charAt(z) == 'b')
					m.getTiles()[j][i] = new Obstacle();
				else if (lines.charAt(z) == 'a')
					m.getTiles()[j][i] = new Ground();
				else if (lines.charAt(z) == 'c')
					m.getTiles()[j][i] = new Gap();
				z++;
			}
		}
		br.close();

		loadZonesOnMap(m, m.getLength() / m.getZoneLength());
		return m;
	}


//	public static String fromConditionToString(Condition e) {
//		switch (e) {
//		case STANDING:
//			return "STANDING";
//		case RUNNING:
//			return "RUNNING";
//		case WALKING:
//			return "WALKING";
//		case JUMPING:
//			return "JUMPING";
//		case UNREADY:
//			return "UNREADY";
//		case FIRING:
//			return "FIRING";
//		case STRIKING:
//			return "STRIKING";
//		case DEAD:
//			return "DEAD";
//		case HIT:
//			return "HIT";
//		case MOVINGFORWARD:
//			return "MOVINGFORWARD";
//		default:
//			return "";
//		}
//	}
//
//	public static String fromDirectionToString(Direction d) {
//		switch (d) {
//		case UP:
//			return "UP";
//		case DOWN:
//			return "DOWN";
//		case RIGHT:
//			return "RIGHT";
//		case LEFT:
//			return "LEFT";
//		case NONE:
//			return "NONE";
//		default:
//			return "";
//		}
//	}
	
	public static void main(String[] args) throws IOException {
		// Creator CREATOR = new Creator();

		// Maps map = new Maps();
		//
		// CREATOR.createMapTiles(99, map);
		// CREATOR.loadZonesOnMap(map, 11);
		// CREATOR.drawBorderOnMap(map);
		//
		// CREATOR.displayZones(map);
		// CREATOR.displayZoneTiles(map, new Position(3, 3));
		// System.out.println(map.locateTileOnZone(new Position(15, 5))
		// .getPosition());
		//
		// CREATOR.writeMapToFile(map, "BattleField");

		// Maps map2 = CREATOR.readMapFromFile("BattleField");
	}
}
