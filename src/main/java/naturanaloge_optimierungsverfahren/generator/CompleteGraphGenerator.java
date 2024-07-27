package naturanaloge_optimierungsverfahren.generator;

import org.graphstream.graph.Edge;
import org.graphstream.graph.Graph;
import org.graphstream.graph.implementations.SingleGraph;

public class CompleteGraphGenerator {

    public static Graph generate(String id, int nodeCount, double minWeight, double maxWeight) {
        if (nodeCount < 4) throw new IllegalArgumentException();
        Graph graph = new SingleGraph(id);
        graph.setAttribute("ui.stylesheet", "url("+ CompleteGraphGenerator.class.getResource("/stylesheets/nodesAndEdges.css")+")");
        graph.setStrict(false);
        graph.setAutoCreate(true);
        graph.addNode("0");
        for (int source = 0; source < nodeCount-1; source++) {
            graph.getNode(source).setAttribute("ui.label", graph.getNode(source).getId());
            for (int target = source+1; target < nodeCount; target++){
                String edgeId = source + "+" + target;
                Edge edge = graph.addEdge(edgeId, Integer.toString(source), Integer.toString(target));
                double weight = randomWeight(minWeight, maxWeight);
                edge.setAttribute("weight", weight);
                edge.setAttribute("ui.label", weight);
            }
        }
        graph.getNode(nodeCount-1).setAttribute("ui.label", graph.getNode(nodeCount-1).getId());
        return graph;
    }

    private static double randomWeight(double minWeight, double maxWeight) {
        return (Math.random() * (maxWeight - minWeight) + minWeight);
    }
}
