package naturanaloge_optimierungsverfahren.client;

import naturanaloge_optimierungsverfahren.algo.GeneticAlgorithms;
import naturanaloge_optimierungsverfahren.algo.NearestInsertion;
import naturanaloge_optimierungsverfahren.logic.Tour;
import org.graphstream.graph.Graph;

import static naturanaloge_optimierungsverfahren.generator.CompleteGraphGenerator.generate;

public class TSPClient {

    public static void main(String[] args) {
        System.setProperty("org.graphstream.ui", "swing");

        gaVSni();

    }

    private static void recombinationTest() {
        for (int i = 0; i < 10; i++) {
            Graph g = generate("g", 20, 10, 50);
            GeneticAlgorithms ga1 = new GeneticAlgorithms(g, 100, 0, 0);
            GeneticAlgorithms ga2 = new GeneticAlgorithms(g, 100, 0, 1);

            Tour optimum1 = ga1.findOptimum(10);
            Tour optimum2 = ga2.findOptimum(10);

            System.out.println("-------------------------------------------------------------------------------------");
            System.out.println("OrderCrossover: " + optimum1.getPathWeight());
            System.out.println("CycleCrossover: " + optimum2.getPathWeight());
            System.out.println("-------------------------------------------------------------------------------------");
        }
    }

    public static void mutationTest(){
        for (int i = 0; i < 10; i++) {
            Graph g = generate("g", 20, 10, 50);
            GeneticAlgorithms ga1 = new GeneticAlgorithms(g, 100, 0, 0);
            GeneticAlgorithms ga2 = new GeneticAlgorithms(g, 100, 1, 0);
            GeneticAlgorithms ga3 = new GeneticAlgorithms(g, 100, 2, 0);
            Tour optimum1 = ga1.findOptimum(10);
            Tour optimum2 = ga2.findOptimum(10);
            Tour optimum3 = ga3.findOptimum(10);
            System.out.println("-------------------------------------------------------------------------------------");
            System.out.println("Invert: " + optimum1.getPathWeight());
            System.out.println("Switch: " + optimum2.getPathWeight());
            System.out.println("Shift: " + optimum3.getPathWeight());
            System.out.println("-------------------------------------------------------------------------------------");
        }
    }

    public static void gaVSni(){
        for (int i = 0; i < 10; i++) {
            Graph g = generate("g", 50, 10, 50);
            GeneticAlgorithms ga = new GeneticAlgorithms(g, 100, 0, 0);
            NearestInsertion ni = new NearestInsertion(g);
            long gaStart = System.currentTimeMillis();
            Tour optimum1 = ga.findOptimum(500);
            long gaEnd = System.currentTimeMillis();
            long gaTime = gaEnd-gaStart;
            long niStart = System.currentTimeMillis();
            Tour optimum2 = ni.solve();
            long niEnd = System.currentTimeMillis();
            long niTime = niEnd-niStart;
            System.out.println("-------------------------------------------------------------------------------------");
            System.out.printf("GeneticAlgorithms: %f | time: %d ms%n", optimum1.getPathWeight(), gaTime);
            System.out.printf("NearestInsertion: %f | time: %d ms%n", optimum2.getPathWeight(), niTime);
            System.out.println("-------------------------------------------------------------------------------------");
        }
    }
}
