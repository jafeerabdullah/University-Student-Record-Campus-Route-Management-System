package graph;

/** One direction of a road in a custom linked adjacency list. */
public final class Edge {
    private final Location destination;
    private final double distance;
    Edge next;

    Edge(Location destination, double distance) {
        this.destination = destination;
        this.distance = distance;
    }

    public Location getDestination() { return destination; }
    public double getDistance() { return distance; }
}
