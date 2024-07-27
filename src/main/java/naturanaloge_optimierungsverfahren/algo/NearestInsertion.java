package naturanaloge_optimierungsverfahren.algo;

import naturanaloge_optimierungsverfahren.logic.Tour;
import org.graphstream.graph.Edge;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;

import java.util.*;

/**
 * Findet auch eine optimale Lösung, wobei hier ohne genetische Algorithmen gearbeitet wird
 * Nearest Insertion Algorithm
 * Resultat besser als das doppelte vom optimalen Minimum
 */
public class NearestInsertion {

    Graph g;
    public NearestInsertion(Graph g){
        this.g = g;
    }

    public Tour solve(){
        List<Node> visited = new ArrayList<>();
        Edge shortest = null;
        Optional<Edge> opt = g.edges().min(Comparator.comparingDouble(edge -> Double.parseDouble(String.valueOf(edge.getAttribute("weight")))));
        if (opt.isPresent()) {
            shortest = opt.get();
        }
        assert shortest != null;
        visited.add(shortest.getSourceNode());
        visited.add(shortest.getTargetNode());
        Node[] currentPath = new Node[]{shortest.getSourceNode(), shortest.getTargetNode(), shortest.getSourceNode()};
        for (int i = 0; i < g.getNodeCount()-2; i++) {
            PriorityQueue<Edge> pq = new PriorityQueue<>(Comparator.comparingDouble(edge -> Double.parseDouble(String.valueOf(edge.getAttribute("weight")))));
            for (Node n : visited) {
                pq.addAll(n.edges().toList());
            }
            Edge currentShortest = pq.poll();
            while(visited.contains(currentShortest.getSourceNode()) && visited.contains(currentShortest.getTargetNode())) {
                currentShortest = pq.poll();
            }
            Node toAdd = visited.contains(currentShortest.getSourceNode()) ? currentShortest.getTargetNode() : currentShortest.getSourceNode();
            currentPath = giveBackShortestPath(toAdd, currentPath);
            visited.add(toAdd);
        }
        return new Tour(currentPath);
    }

    public Node[] giveBackShortestPath(Node newNode, Node[] tour){
        Map<Node[], Double> weightMap = new HashMap<>();
        for (int i = 1; i < tour.length; i++) {
            Node[] newPath = new Node[tour.length+1];
            insertNewNodeInBetweenAt(i, newPath, tour, newNode);
            weightMap.put(newPath, countUpPathWeight(newPath));
        }
        Optional<Map.Entry<Node[], Double>> entryWithMinValue = weightMap.entrySet().stream()
                .min(Map.Entry.comparingByValue());

        return entryWithMinValue.map(Map.Entry::getKey).orElse(null);
    }

    private void insertNewNodeInBetweenAt(int i, Node[] newPath, Node[] tour, Node newNode){
        if (i >= 0) System.arraycopy(tour, 0, newPath, 0, i);
        newPath[i] = newNode;
        if (tour.length - i >= 0) System.arraycopy(tour, i, newPath, i + 1, tour.length - i);
    }

    public double countUpPathWeight(Node[] path){
        double weight = 0;
        for (int i = 1; i < path.length; i++) {
            weight += Double.parseDouble(String.valueOf(path[i].getEdgeBetween(path[i-1]).getAttribute("weight")));
        }
        return weight;
    }
}
