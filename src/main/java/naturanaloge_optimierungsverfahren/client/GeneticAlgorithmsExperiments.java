package naturanaloge_optimierungsverfahren.client;

import naturanaloge_optimierungsverfahren.algo.AntColonyOptimization;
import naturanaloge_optimierungsverfahren.algo.GeneticAlgorithms;
import naturanaloge_optimierungsverfahren.algo.NearestInsertion;
import naturanaloge_optimierungsverfahren.logic.Tour;
import org.graphstream.graph.Graph;

import java.io.FileWriter;
import java.io.IOException;

import static naturanaloge_optimierungsverfahren.generator.CompleteGraphGenerator.generate;

public class GeneticAlgorithmsExperiments {
    public static void main(String[] args) {

        // Define the constant parameters
        int populationSize = 100;
        int generations = 500;
        int mutationType = 0;
        int recombinationType = 0;

        acoVSgaVSni(populationSize, generations);
    }

    public static void mutationTypeTest(int populationSize, int generations){
        try (FileWriter writer = new FileWriter("mutation_type_test.csv")) {
            // Write CSV header
            writer.write("MutationType;Run;Time;Result\n");

            for (int i = 0; i < 50; i++) {
                Graph g = generate("g", 50, 10, 30);

                // Invert Mutation
                long startTime = System.nanoTime();
                GeneticAlgorithms ga1 = new GeneticAlgorithms(g, populationSize, 0, 0);
                Tour optimum1 = ga1.findOptimum(generations);
                long endTime = System.nanoTime();
                long duration = endTime - startTime;
                writer.write(String.format("Invert;%d;%d;%f\n", i + 1, duration, optimum1.getPathWeight()));

                // Switch Mutation
                startTime = System.nanoTime();
                GeneticAlgorithms ga2 = new GeneticAlgorithms(g, populationSize, 1, 0);
                Tour optimum2 = ga2.findOptimum(generations);
                endTime = System.nanoTime();
                duration = endTime - startTime;
                writer.write(String.format("Switch;%d;%d;%f\n", i + 1, duration, optimum2.getPathWeight()));

                // Shift Mutation
                startTime = System.nanoTime();
                GeneticAlgorithms ga3 = new GeneticAlgorithms(g, populationSize, 2, 0);
                Tour optimum3 = ga3.findOptimum(generations);
                endTime = System.nanoTime();
                duration = endTime - startTime;
                writer.write(String.format("Shift;%d;%d;%f\n", i + 1, duration, optimum3.getPathWeight()));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static void recombinationTypeTest(int populationSize, int generations){
        try (FileWriter writer = new FileWriter("recombination_type_test.csv")) {
            // Write CSV header
            writer.write("RecombinationType;Run;Time;Result\n");

            for (int i = 0; i < 50; i++) {
                Graph g = generate("g", 50, 10, 30);

                // Order-Crossover
                long startTime = System.nanoTime();
                GeneticAlgorithms ga1 = new GeneticAlgorithms(g, populationSize, 0, 0);
                Tour optimum1 = ga1.findOptimum(generations);
                long endTime = System.nanoTime();
                long duration = endTime - startTime;
                writer.write(String.format("Order-Crossover;%d;%d;%f\n", i + 1, duration, optimum1.getPathWeight()));

                // Cycle-Crossover
                startTime = System.nanoTime();
                GeneticAlgorithms ga2 = new GeneticAlgorithms(g, populationSize, 0, 1);
                Tour optimum2 = ga2.findOptimum(generations);
                endTime = System.nanoTime();
                duration = endTime - startTime;
                writer.write(String.format("Cycle-Crossover;%d;%d;%f\n", i + 1, duration, optimum2.getPathWeight()));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static void recombinationAndMutationRateTest(int populationSize, int generations){
        try (FileWriter writer = new FileWriter("rec_mut_rate_test.csv")) {
            // Write CSV header
            writer.write("RecombinationRate;MutationRate;Run;Result\n");
            double size = 0.1;
            for (int j = 0; j < 5; j++) {
                Graph g = generate("g", 50, 10, 30);
                double mutationRate = 0;
                double recombinationRate = 1;
                for (int i = 0; i <= 10; i++) {

                    GeneticAlgorithms ga1 = new GeneticAlgorithms(g, populationSize, 0, 0, mutationRate, recombinationRate);
                    Tour optimum1 = ga1.findOptimum(generations);
                    writer.write(String.format("%g;%g;%d;%f\n", recombinationRate, mutationRate, j + 1, optimum1.getPathWeight()));

                    mutationRate += size;
                    recombinationRate -= size;

                    System.out.println(j + "-" + i);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static void populationSizeTest(int generations){
        try (FileWriter writer = new FileWriter("population_size_test.csv")) {
            // Write CSV header
            writer.write("Population Size;Run;Time in ns;Result\n");
            for (int j = 1; j <= 5; j++) {
                Graph g = generate("g", 50, 10, 30);
                int populationSize = 10;
                for (int i = 0; i < 10; i++) {

                    long startTime = System.nanoTime();
                    GeneticAlgorithms ga1 = new GeneticAlgorithms(g, populationSize, 0, 0);
                    Tour optimum1 = ga1.findOptimum(generations);
                    long endTime = System.nanoTime();
                    long duration = endTime - startTime;
                    writer.write(String.format("%d;%d;%d;%f\n", populationSize, j, duration, optimum1.getPathWeight()));
                    populationSize *= 2;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static void generationsTest(int populationSize){
        try (FileWriter writer = new FileWriter("generations_test.csv")) {
            // Write CSV header
            writer.write("Generations;Run;Time in ns;Result\n");
            for (int j = 1; j <= 5; j++) {
                Graph g = generate("g", 50, 10, 30);
                int generations = 10;
                for (int i = 0; i < 10; i++) {

                    long startTime = System.nanoTime();
                    GeneticAlgorithms ga1 = new GeneticAlgorithms(g, populationSize, 0, 0);
                    Tour optimum1 = ga1.findOptimum(generations);
                    long endTime = System.nanoTime();
                    long duration = endTime - startTime;
                    writer.write(String.format("%d;%d;%d;%f\n", generations, j, duration, optimum1.getPathWeight()));
                    generations *= 2;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void acoVSgaVSni(int populationSize, int generations){
        try (FileWriter writer = new FileWriter("aco_ga_ni.csv")) {
            // Write CSV header
            writer.write("Algorithm;Run;Time;Result\n");

            for (int i = 0; i < 50; i++) {
                Graph g = generate("g", 50, 10, 30);

                // GA
                long startTime = System.nanoTime();
                GeneticAlgorithms ga1 = new GeneticAlgorithms(g, populationSize, 0, 0);
                Tour optimum1 = ga1.findOptimum(generations);
                long endTime = System.nanoTime();
                long duration = endTime - startTime;
                writer.write(String.format("GA;%d;%d;%f\n", i + 1, duration, optimum1.getPathWeight()));

                // ACO
                startTime = System.nanoTime();
                AntColonyOptimization aoc = new AntColonyOptimization(g, 10, 100, 0.1, 1.0, 2.0);
                aoc.solve();
                endTime = System.nanoTime();
                duration = endTime - startTime;
                writer.write(String.format("ACO;%d;%d;%f\n", i + 1, duration, aoc.getBestTourLength()));

                // NearestInsertion
                startTime = System.nanoTime();
                NearestInsertion ni = new NearestInsertion(g);
                Tour optimum3 = ni.solve();
                endTime = System.nanoTime();
                duration = endTime - startTime;
                writer.write(String.format("Nearest Insertion;%d;%d;%f\n", i + 1, duration, optimum3.getPathWeight()));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
