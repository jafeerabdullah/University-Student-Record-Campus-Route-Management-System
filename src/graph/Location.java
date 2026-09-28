package graph;

/** Graph-owned vertex; adjacency links stay internal to the graph package. */
public final class Location {
    private final String name;
    int index;
    Edge firstEdge;
    Edge lastEdge;

    Location(String name, int index) {
        this.name = name;
        this.index = index;
    }

    public String getName() { return name; }

    @Override
    public String toString() { return name; }
}
