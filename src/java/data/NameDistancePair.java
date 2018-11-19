package data;

/**
 * A pure POJO, only used to return the string and its distance at the same time, in order to not having to calculate the distance twice.
 */
public class NameDistancePair {

	private String name;
	private int distance;

	public NameDistancePair(String name, int distance) {
		this.name = name;
		this.distance = distance;
	}

	public String getName() {
		return name;
	}

	public int getDistance() {
		return distance;
	}
}
