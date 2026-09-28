package graph;

import java.util.Locale;

/** Weighted undirected graph with an expandable vertex array and linked edges. */
public final class CampusRouteGraph {
    private Location[] locations = new Location[8];
    private int size;
    private int roadCount;

    private String validName(String name) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Location name cannot be empty.");
        }
        return name.trim();
    }

    public Location findLocation(String name) {
        String normalized = validName(name);
        for (int i = 0; i < size; i++) {
            if (locations[i].getName().equalsIgnoreCase(normalized)) {
                return locations[i];
            }
        }
        return null;
    }

    private Location requireLocation(String name) {
        Location location = findLocation(name);
        if (location == null) {
            throw new IllegalArgumentException("Location not found: " + name);
        }
        return location;
    }

    public boolean addLocation(String name) {
        String normalized = validName(name);
        if (findLocation(normalized) != null) { return false; }
        if (size == locations.length) {
            Location[] expanded = new Location[locations.length * 2];
            System.arraycopy(locations, 0, expanded, 0, size);
            locations = expanded;
        }
        locations[size] = new Location(normalized, size);
        size++;
        return true;
    }

    public boolean removeLocation(String name) {
        Location removed = findLocation(name);
        if (removed == null) { return false; }
        // Remove every incoming edge; each removed pair counts as one road.
        for (int i = 0; i < size; i++) {
            if (locations[i] != removed && unlink(locations[i], removed)) {
                roadCount--;
            }
        }
        int index = removed.index;
        for (int i = index; i < size - 1; i++) {
            locations[i] = locations[i + 1];
            locations[i].index = i;
        }
        locations[--size] = null;
        removed.firstEdge = null;
        removed.lastEdge = null;
        return true;
    }

    public boolean addRoad(String fromName, String toName, double distance) {
        if (!Double.isFinite(distance) || distance <= 0.0) {
            throw new IllegalArgumentException("Distance must be a finite number greater than zero.");
        }
        Location from = requireLocation(fromName);
        Location to = requireLocation(toName);
        if (from == to) {
            throw new IllegalArgumentException("A road must connect two different locations.");
        }
        if (findEdge(from, to) != null) { return false; }
        append(from, new Edge(to, distance));
        append(to, new Edge(from, distance));
        roadCount++;
        return true;
    }

    private Edge findEdge(Location from, Location to) {
        for (Edge edge = from.firstEdge; edge != null; edge = edge.next) {
            if (edge.getDestination() == to) { return edge; }
        }
        return null;
    }

    private void append(Location location, Edge edge) {
        if (location.lastEdge == null) {
            location.firstEdge = edge;
        } else {
            location.lastEdge.next = edge;
        }
        location.lastEdge = edge;
    }

    public boolean removeRoad(String fromName, String toName) {
        Location from = requireLocation(fromName);
        Location to = requireLocation(toName);
        if (!unlink(from, to)) { return false; }
        unlink(to, from);
        roadCount--;
        return true;
    }

    private boolean unlink(Location from, Location to) {
        Edge previous = null;
        Edge edge = from.firstEdge;
        while (edge != null) {
            if (edge.getDestination() == to) {
                if (previous == null) {
                    from.firstEdge = edge.next;
                } else {
                    previous.next = edge.next;
                }
                if (from.lastEdge == edge) { from.lastEdge = previous; }
                return true;
            }
            previous = edge;
            edge = edge.next;
        }
        return false;
    }

    public double distanceBetween(String fromName, String toName) {
        Edge edge = findEdge(requireLocation(fromName), requireLocation(toName));
        return edge == null ? Double.POSITIVE_INFINITY : edge.getDistance();
    }

    /** Manual array queue; a vertex is marked when enqueued, preventing duplicates. */
    public String[] bfs(String startName) {
        Location start = requireLocation(startName);
        boolean[] visited = new boolean[size];
        int[] queue = new int[size];
        int front = 0;
        int rear = 0;
        String[] order = new String[size];
        int count = 0;
        visited[start.index] = true;
        queue[rear++] = start.index;
        while (front < rear) {
            Location current = locations[queue[front++]];
            order[count++] = current.getName();
            for (Edge edge = current.firstEdge; edge != null; edge = edge.next) {
                int index = edge.getDestination().index;
                if (!visited[index]) {
                    visited[index] = true;
                    queue[rear++] = index;
                }
            }
        }
        return trim(order, count);
    }

    /** Manual stack of adjacency cursors simulates recursive depth-first traversal. */
    public String[] dfs(String startName) {
        Location start = requireLocation(startName);
        boolean[] visited = new boolean[size];
        Edge[] stack = new Edge[size];
        int top = 0;
        stack[top] = start.firstEdge;
        String[] order = new String[size];
        int count = 0;
        order[count++] = start.getName();
        visited[start.index] = true;
        while (top >= 0) {
            Edge edge = stack[top];
            if (edge == null) {
                top--;
            } else {
                stack[top] = edge.next;
                Location destination = edge.getDestination();
                if (!visited[destination.index]) {
                    visited[destination.index] = true;
                    order[count++] = destination.getName();
                    stack[++top] = destination.firstEdge;
                }
            }
        }
        return trim(order, count);
    }

    private String[] trim(String[] source, int count) {
        String[] result = new String[count];
        System.arraycopy(source, 0, result, 0, count);
        return result;
    }

    public String[] locationNames() {
        String[] result = new String[size];
        for (int i = 0; i < size; i++) { result[i] = locations[i].getName(); }
        return result;
    }

    public String describeConnections() {
        if (size == 0) { return "No campus locations.\n"; }
        StringBuilder text = new StringBuilder("Campus connections (bidirectional; metres)\n");
        for (int i = 0; i < size; i++) {
            Location location = locations[i];
            text.append(location.getName()).append(" -> ");
            if (location.firstEdge == null) { text.append("(no roads)"); }
            for (Edge edge = location.firstEdge; edge != null; edge = edge.next) {
                text.append(edge.getDestination().getName()).append(" (")
                        .append(String.format(Locale.ROOT, "%.2f", edge.getDistance())).append(" m)");
                if (edge.next != null) { text.append(", "); }
            }
            text.append('\n');
        }
        text.append(size).append(" locations, ").append(roadCount).append(" roads\n");
        return text.toString();
    }

    public int size() { return size; }
    public int roadCount() { return roadCount; }
}
