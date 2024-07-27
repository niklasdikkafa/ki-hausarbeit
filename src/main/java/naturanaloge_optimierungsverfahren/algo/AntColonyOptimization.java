package naturanaloge_optimierungsverfahren.algo;

import org.graphstream.graph.Edge;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.graphstream.graph.implementations.SingleGraph;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class AntColonyOptimization {
    private Graph graph;
    private int numAnts;
    private int numIterations;
    private double evaporationRate;
    private double alpha;
    private double beta;
    private double[][] pheromones;
    private List<Node> bestTour;
    private double bestTourLength = Double.MAX_VALUE;

    public AntColonyOptimization(Graph graph, int numAnts, int numIterations, double evaporationRate, double alpha, double beta) {
        this.graph = graph;
        this.numAnts = numAnts;
        this.numIterations = numIterations;
        this.evaporationRate = evaporationRate;
        this.alpha = alpha;
        this.beta = beta;
        this.bestTour = new ArrayList<>();
        initializePheromones();
    }

    private void initializePheromones() {
        int size = graph.getEdgeCount();
        pheromones = new double[size][size];
        for (int i = 0; i < size; i++) {
            Arrays.fill(pheromones[i], 1.0);
        }
    }

    public void solve() {
        for (int iteration = 0; iteration < numIterations; iteration++) {
            List<List<Node>> allTours = new ArrayList<>();

            for (int ant = 0; ant < numAnts; ant++) {
                List<Node> tour = generateTour();
                allTours.add(tour);
            }

            for (List<Node> tour : allTours) {
                double tourLength = calculateTourLength(tour);
                if (tourLength < bestTourLength) {
                    bestTour = tour;
                    bestTourLength = tourLength;
                }
                updatePheromones(tour, tourLength);
            }

            evaporatePheromones();
        }
    }

    private List<Node> generateTour() {
        List<Node> tour = new ArrayList<>();
        Node startNode = graph.getNode(0);
        tour.add(startNode);

        Node currentNode = startNode;
        while (tour.size() < graph.getNodeCount()) {
            Node nextNode = selectNextNode(currentNode, tour);
            tour.add(nextNode);
            currentNode = nextNode;
        }

        tour.add(startNode); // Rückkehr zum Startknoten
        return tour;
    }

    private Node selectNextNode(Node currentNode, List<Node> tour) {
        List<Node> neighbors = new ArrayList<>();
        List<Edge> currentNodeEdges = currentNode.edges().toList();
        for (Edge edge : currentNodeEdges) {
            Node neighbor = edge.getOpposite(currentNode);
            if (!tour.contains(neighbor)) {
                neighbors.add(neighbor);
            }
        }

        double[] probabilities = new double[neighbors.size()];
        double sum = 0.0;
        for (int i = 0; i < neighbors.size(); i++) {
            Node neighbor = neighbors.get(i);
            double pheromone = pheromones[currentNode.getIndex()][neighbor.getIndex()];
            Edge edge = currentNode.getEdgeBetween(neighbor);
            double distance = Double.parseDouble(String.valueOf(edge.getAttribute("weight")));
            probabilities[i] = Math.pow(pheromone, alpha) * Math.pow(1.0 / distance, beta);
            sum += probabilities[i];
        }

        double rand = Math.random() * sum;
        for (int i = 0; i < probabilities.length; i++) {
            rand -= probabilities[i];
            if (rand <= 0) {
                return neighbors.get(i);
            }
        }

        return neighbors.get(neighbors.size() - 1); // Rückfall
    }

    private double calculateTourLength(List<Node> tour) {
        double length = 0.0;
        for (int i = 0; i < tour.size() - 1; i++) {
            Node currentNode = tour.get(i);
            Node nextNode = tour.get(i + 1);
            Edge edge = currentNode.getEdgeBetween(nextNode);
            length += Double.parseDouble(String.valueOf(edge.getAttribute("weight")));
        }
        return length;
    }

    private void updatePheromones(List<Node> tour, double tourLength) {
        for (int i = 0; i < tour.size() - 1; i++) {
            Node currentNode = tour.get(i);
            Node nextNode = tour.get(i + 1);
            pheromones[currentNode.getIndex()][nextNode.getIndex()] += 1.0 / tourLength;
        }
    }

    private void evaporatePheromones() {
        for (int i = 0; i < pheromones.length; i++) {
            for (int j = 0; j < pheromones[i].length; j++) {
                pheromones[i][j] *= (1.0 - evaporationRate);
            }
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        if (!bestTour.isEmpty()) sb.append(bestTour.get(0));
        for (int i = 1; i < bestTour.size(); i++) {
            sb.append(" - ").append(bestTour.get(i-1).getEdgeBetween(bestTour.get(i)).getId()).append(" - ");
            sb.append(bestTour.get(i));
        }
        sb.append(" || - path weight: ").append(bestTourLength);
        return sb.toString();
    }

    public double getBestTourLength() {
        return bestTourLength;
    }

    public static void main(String[] args) {
        Graph graph = new SingleGraph("TSP");

        // Beispielstädte hinzufügen
        String[] cities = {"A", "B", "C", "D"};
        for (String city : cities) {
            graph.addNode(city).setAttribute("ui.label", city);
        }

        // Kanten mit Entfernungen hinzufügen
        graph.addEdge("AB", "A", "B").setAttribute("weight", 10);
        graph.addEdge("AC", "A", "C").setAttribute("weight", 15);
        graph.addEdge("AD", "A", "D").setAttribute("weight", 20);
        graph.addEdge("BC", "B", "C").setAttribute("weight", 35);
        graph.addEdge("BD", "B", "D").setAttribute("weight", 25);
        graph.addEdge("CD", "C", "D").setAttribute("weight", 30);


        AntColonyOptimization aco = new AntColonyOptimization(graph, 10, 100, 0.1, 1.0, 2.0);
        aco.solve();
        System.out.println(aco);
    }

}
