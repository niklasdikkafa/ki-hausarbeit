package naturanaloge_optimierungsverfahren.logic;

import naturanaloge_optimierungsverfahren.generator.CompleteGraphGenerator;
import org.graphstream.graph.Edge;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;

import java.util.*;
import java.util.stream.Collectors;

public class Tour implements Comparable<Tour> {

    private final double fitness;

    private double pathWeight;
    private final Node[] nodePath;
    private final Edge[] edgePath;


    /**
     * Initialisiert random Rundreise für g
     * @param g
     */
    public Tour(Graph g){
        if (g.getEdgeCount() != (g.getNodeCount()*(g.getNodeCount()-1))/2) throw new IllegalArgumentException("g must be complete");
        List<Node> nodes = g.nodes().collect(Collectors.toList());
        int numberOfNodes = g.getNodeCount();
        Random random = new Random();
        nodePath = new Node[numberOfNodes+1];
        edgePath = new Edge[numberOfNodes];
        for (int i = 0; i < numberOfNodes; i++){
            Node current = nodes.get(random.nextInt(nodes.size()));
            if (i == 0) {
                nodePath[0] = current;
                nodePath[numberOfNodes] = current;
            } else {
                nodePath[i] = current;
                Edge edge = nodePath[i-1].getEdgeBetween(current);
                edgePath[i-1] = edge;
            }
            nodes.remove(current);
        }
        Node endNode = nodePath[numberOfNodes];
        Edge edge = nodePath[numberOfNodes-1].getEdgeBetween(endNode);
        edgePath[numberOfNodes-1] = edge;
        calcPathWeight("weight");
        fitness = 1 / pathWeight;
    }

    public Tour(Node[] nodePath) {
        this.nodePath = nodePath;
        edgePath = new Edge[nodePath.length-1];
        for (int i = 1; i < nodePath.length; i++) {
            Edge edge = nodePath[i-1].getEdgeBetween(nodePath[i]);
            edgePath[i-1] = edge;
        }
        calcPathWeight("weight");
        fitness = 1 / pathWeight;
    }

    public void calcPathWeight(String characteristic) {
        double d = 0.0;
        for (Edge l : edgePath) {
            d += (double) l.getAttribute(characteristic, Number.class);
        }
        pathWeight = d;
    }

    @Override
    public int compareTo(Tour tour) {
        return Double.compare(this.getPathWeight(), tour.getPathWeight());
    }

    public double getFitness() {
        return fitness;
    }

    public double getPathWeight() {
        return pathWeight;
    }

//    @Override
//    public boolean equals(Object o) {
//        if (this == o) return true;
//        if (o == null || getClass() != o.getClass()) return false;
//        Tour tour = (Tour) o;
//        return Arrays.equals(nodePath, tour.nodePath);
//    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(nodePath);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        int ct = 0;
        for (Node n : nodePath) {
            sb.append(n.getId());
            if (ct < edgePath.length) {
                sb.append(" - ").append(edgePath[ct].getId()).append(" - ");
            }
            ct++;
        }
        sb.append(" || - path weight: ").append(pathWeight);
        sb.append(" || - fitness: ").append(fitness);
        return sb.toString();
    }

    public Tour recombination(int recombinationCase, Tour t2) {
        Tour newTour = null;
        switch (recombinationCase){
            case 0 -> newTour = orderCrossover(t2);
            case 1 -> newTour = cycleCrossover(t2);
        }
        return newTour;
    }

    private Tour cycleCrossover(Tour t2) {
        if (nodePath.length != t2.nodePath.length) throw new IllegalArgumentException();
        Set<Node> visited = new HashSet<>();
        Node[] newTour = new Node[nodePath.length];
        Random random = new Random();
        int currentIndex = random.nextInt(nodePath.length-1);
        Node currentNode = nodePath[currentIndex];

        while (!visited.contains(currentNode)){
            visited.add(currentNode);
            newTour[currentIndex] = t2.nodePath[currentIndex];
            currentIndex = findIndexOfNode(t2.nodePath[currentIndex]);
            currentNode = nodePath[currentIndex];
        }
        for (int i = 0; i < newTour.length - 1; i++) {
            if (newTour[i] == null) newTour[i] = nodePath[i];
        }
        newTour[newTour.length-1] = newTour[0];
        return new Tour(newTour);
    }

    private int findIndexOfNode(Node node) {
        for (int i = 0; i < nodePath.length; i++) {
            if (nodePath[i].equals(node)) return i;
        }
        return -1;
    }

    /**
     * Rekombination
     * @param t2
     * @return new Tour
     */
    public Tour orderCrossover(Tour t2) {
        if (nodePath.length != t2.nodePath.length) throw new IllegalArgumentException();
        int tourPartSize = this.nodePath.length / 2;
        Node[] newTour = new Node[nodePath.length];
        Random random = new Random();
        int start = random.nextInt(nodePath.length-tourPartSize-1);
        List<Node> toDelete = new ArrayList<>(); // Nodes, die wir aus t2 "löschen"
        for (int i = start; i < start + tourPartSize; i++) {
            newTour[i] = nodePath[i];
            toDelete.add(nodePath[i]);
        }
        int spotsToFill = nodePath.length - tourPartSize - 1; // -1, weil letztes Element wird extern eingefügt
        int currentNullIndex = 0;
        for (int i = 0; i < t2.nodePath.length-1; i++){
            if (spotsToFill == 0) break;
            if (!toDelete.contains(t2.nodePath[i])) {
                while (newTour[currentNullIndex++] != null);
                newTour[currentNullIndex-1] = t2.nodePath[i];
                spotsToFill--;
            }
        }
        newTour[newTour.length-1] = newTour[0];
        return new Tour(newTour);
    }


    public Tour mutation(int mutationCase) {
        Node[] newTour = nodePath.clone();
        Random random = new Random();
        switch (mutationCase) {
            case 0 -> invertPathPart(newTour, random);
            case 1 -> switchNodes(newTour, random);
            case 2 -> shift(newTour, random);
        }
        return new Tour(newTour);
    }

    /**
     * Vertauschen zweier aufeinanderfolgender Knoten
     * @param newTour
     * @param random
     */
    private void switchNodes(Node[] newTour, Random random) {
        int switchIndex = random.nextInt(nodePath.length-2);
        Node temp = newTour[switchIndex];
        newTour[switchIndex] = newTour[switchIndex+1];
        newTour[switchIndex+1] = temp;

        newTour[newTour.length-1] = newTour[0];
    }

    /**
     * Invertieren einer Teilroute
     * @param newTour
     * @param random
     */
    private void invertPathPart(Node[] newTour, Random random) {
        int pathPartSize = random.nextInt(3, nodePath.length);
        int start = random.nextInt(nodePath.length-pathPartSize);
        int end = start + pathPartSize - 1;
        while (start < end) {
            Node temp = newTour[start];
            newTour[start] = newTour[end];
            newTour[end] = temp;
            start++;
            end--;
        }
        newTour[newTour.length-1] = newTour[0];
    }

    /**
     * Verschieben der Routenrepräsentation
     * @param newTour
     * @param random
     */
    private void shift(Node[] newTour, Random random) {
        int n = nodePath.length;
        int maxShiftableElements = n - 3;
        int numElementsToShift = random.nextInt(maxShiftableElements) + 1; // wie viele Elemente sollen verschoben werden
        int shiftIndex = random.nextInt(n-1-numElementsToShift); // ab welchen Index soll verschoben werden (nach rechts)
        int shiftLength = random.nextInt(n-1-numElementsToShift-shiftIndex) + 1 ; // wie viele Stellen soll verschoben werden
        Node[] temp = new Node[numElementsToShift];
        System.arraycopy(newTour, shiftIndex, temp, 0, numElementsToShift);
        System.arraycopy(newTour, shiftIndex+numElementsToShift, newTour, shiftIndex, shiftLength);
        System.arraycopy(temp, 0, newTour, shiftIndex+shiftLength, numElementsToShift);
        newTour[newTour.length-1] = newTour[0];
    }

    public static void main(String[] args) {
        Graph g = CompleteGraphGenerator.generate("g", 10, 10,20);
        Tour t1 = new Tour(g);
        Tour t2 = new Tour(g);
        Tour t3 = t1.cycleCrossover(t2);
        System.out.println(t1);
        System.out.println(t2);
        System.out.println(t3);
        Tour t4 = t1.mutation(2);
        System.out.println(Arrays.toString(t1.nodePath));
        System.out.println(Arrays.toString(t4.nodePath));
    }

}
