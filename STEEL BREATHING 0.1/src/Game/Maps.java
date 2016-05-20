package Game;

import java.util.HashMap;

public class Maps {
	private Element[][] tiles;
	private Zone[][] zones;

	private int length;
	private int zoneLength;
	
	private HashMap<Position, RemoteAttack> remoteAttacks;
	
	public Maps() {
		remoteAttacks = new HashMap<>();
	}

	public Maps(Element[][] e) {
		this.tiles = e;
	}

	public Element[][] getTiles() {
		return tiles;
	}

	public void setTiles(Element[][] tiles) {
		this.tiles = tiles;
	}

	public Zone[][] getZones() {
		return zones;
	}

	public void setZones(Zone[][] zones) {
		this.zones = zones;
	}

	public int getLength() {
		return length;
	}

	public void setLength(int length) {
		this.length = length;
	}

	public int getZoneLength() {
		return zoneLength;
	}

	public void setZoneLength(int zoneLength) {
		this.zoneLength = zoneLength;
	}

	
	public HashMap<Position, RemoteAttack> getRemoteAttacks() {
		return remoteAttacks;
	}

	public void setRemoteAttacks(HashMap<Position, RemoteAttack> remoteAttacks) {
		this.remoteAttacks = remoteAttacks;
	}

	public void displayZonesNames() {
		for (int i = 0; i < length; i++) {
			for (int j = 0; j < length; j++) {
				System.out.print(zones[j][i].getName()
						+ zones[j][i].getPosition() + "   ");
			}
			System.out.println();
			System.out.println();
		}
	}

	public Zone locateTileOnZone(Position p) {
		Position zonePosition = new Position();
		int i = p.getI();
		int j = p.getJ();
		zonePosition.setI(i / zoneLength);
		zonePosition.setJ(j / zoneLength);
		return zones[zonePosition.getI()][zonePosition.getJ()];
	}
}
