package byow.Core;

import java.util.List;
import java.util.Random;

/**
 * Class that represents the relationship between rooms in an undirected graph.
 */
public class RoomGraph {
    private List<Room> _roomList;
    private double[][] _adjMatrix;
    private Random _random;

    public RoomGraph(List<Room> roomList, Random random) {
        _roomList = roomList;
        _random = random;
        int vertexCount = roomList.size();
        _adjMatrix = new double[vertexCount][vertexCount];
        for (int i = 0; i < vertexCount; i++) {
            for (int j = 0; j < vertexCount; j++) {
                int[] rowRoom = _roomList.get(i).getCenter();
                int[] colRoom = _roomList.get(j).getCenter();
                //construct adjacency matrix using Euclidean distance between room centres.
                _adjMatrix[i][j] = Math.sqrt(Math.pow(rowRoom[0] - colRoom[0], 2)
                        + Math.pow(rowRoom[1] - colRoom[1], 2));
            }
        }
    }

    @Override
    public String toString() {
        int vertexCount = _roomList.size();
        StringBuilder output = new StringBuilder();
        for (Room room : _roomList) {
            output.append(room.toString());
            output.append("\n");
        }
        for (int i = 0; i < vertexCount; i++) {
            for (int j = 0; j < vertexCount; j++) {
                output.append(_adjMatrix[i][j]);
                output.append(",");
            }
            output.append("\n");
        }
        return output.toString();
    }

    /**
     * Modifies the RoomGraph in-place to produce a Minimum Spanning Tree.
     * Uses Prim's algorithm.
     * Source:
     * https://stackoverflow.com/questions/
     * 4440251/minimal-spanning-tree-from-adjacency-matrix-in-java
     */
    public void pa() {
        int numberOfVertices = _roomList.size();
        int[] source = new int[numberOfVertices];
        double[] dist = new double[numberOfVertices];
        boolean[] indicators = new boolean[numberOfVertices];  //if true, vertex i is in tree T
        double[][] mst = new double[numberOfVertices][numberOfVertices];

        // Mark all vertices as NOT being in the minimum spanning tree
        for (int i = 0; i < numberOfVertices; i++) {
            indicators[i] = false;
            dist[i] = Double.POSITIVE_INFINITY;
        }

        //we start with vertex number 0
        indicators[0] = true;
        dist[0] = 0;
        int bestNeighbour = 0;
        // lastly added vertex to the tree T
        double minDist;

        for (int i = 0; i < numberOfVertices - 1; i++) {
            minDist = Double.POSITIVE_INFINITY;

            for (int j = 0; j < numberOfVertices; j++) {
                // fill dist[] based on distance to bestNeighbour vertex
                if (!indicators[j]) {
                    double weight = _adjMatrix[bestNeighbour][j];
                    if (weight < dist[j]) {
                        source[j] = bestNeighbour;
                        dist[j] = weight;
                    }
                }
            }

            for (int j = 0; j < numberOfVertices; j++) {
                // find index of min in dist[]
                if (!indicators[j]) {
                    if (dist[j] < minDist) {
                        bestNeighbour = j;
                        minDist = dist[j];
                    }
                }
            }
            if (bestNeighbour != 0) {
                //add the edge (bestNeighbour, dist[bestNeighbour]) to tree T
                mst[source[bestNeighbour]][bestNeighbour] = dist[bestNeighbour];
                indicators[bestNeighbour] = true;
            }
        }
        _adjMatrix = mst; // modify in-place
    }

    /**
     * After calling PA, call this to add up to n edges randomly into the graph.
     *
     * @param n
     */
    public void addRandomEdges(int n) {
        int count = 0;
        int numberOfVertices = _roomList.size();
        while (count < n) {
            int start = _random.nextInt(numberOfVertices); // pick a random start node
            int end = _random.nextInt(numberOfVertices); // pick a random finish node
            if (_adjMatrix[start][end] != 0.0 || _adjMatrix[end][start] != 0.0) {
                continue; // don't replace an edge that's already there!
            }
            // add an edge of weight 1 (note that this messes up distance)
            _adjMatrix[start][end] = 1;
            count++;
        }
    }

    public List<Room> getRoomList() {
        return _roomList;
    }

    public double[][] getAdjMatrix() {
        return _adjMatrix;
    }
}
