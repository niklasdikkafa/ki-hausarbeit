package naturanaloge_optimierungsverfahren.algo;

import naturanaloge_optimierungsverfahren.generator.CompleteGraphGenerator;
import naturanaloge_optimierungsverfahren.logic.Tour;
import org.graphstream.graph.Graph;

import java.util.*;

public class GeneticAlgorithms {

    Graph g;
    List<Tour> population;
    PriorityQueue<Tour> pq;
    Random random;

    int mutationType;
    int recombinationType;
    double mutationRate;
    double recombinationRate;

    public GeneticAlgorithms(Graph g, int populationSize, int mutationType, int recombinationType) {
        if (mutationType >= 3) throw new IllegalArgumentException();
        if (recombinationType >= 2) throw new IllegalArgumentException();
        this.g = g;
        population = new ArrayList<>();
        pq = new PriorityQueue<>(Tour::compareTo);
        random = new Random();
        generateStartPopulation(populationSize);
        this.mutationType = mutationType;
        this.recombinationType = recombinationType;
        this.mutationRate = 0.5;
        this.recombinationRate = 0.5;
    }

    public GeneticAlgorithms(Graph g, int populationSize, int mutationType, int recombinationType, double mutationRate,
                             double recombinationRate) {
        if (mutationType >= 3) throw new IllegalArgumentException();
        if (recombinationType >= 2) throw new IllegalArgumentException();
        this.g = g;
        population = new ArrayList<>();
        pq = new PriorityQueue<>(Tour::compareTo);
        random = new Random();
        generateStartPopulation(populationSize);
        this.mutationType = mutationType;
        this.recombinationType = recombinationType;
        this.recombinationRate = recombinationRate;
        this.mutationRate = mutationRate;
    }

    public Tour findOptimum(int generations){
        Tour res = null;
        for (int i = 0; i < generations; i++) {
            //System.out.println("start with population size " + population.size());
            //System.out.println("start selection");
            survivalOfTheFittest();
            int currentPopulationSize = population.size();
            //System.out.println("start recombination");
            recombination(currentPopulationSize);
            //System.out.println("start mutation");
            mutation(currentPopulationSize);
            res = pq.peek();
        }
        return res;
    }

    private void generateStartPopulation(int populationSize){
        for (int i = 0; i < populationSize; i++){
            Tour t = new Tour(g);
            population.add(t);
            pq.add(t);
        }
    }

    private void recombination(int size){
        List<Tour> toAdd = new ArrayList<>();

        for (int i = 0; i < size * recombinationRate; i++) {
            Tour t1 = population.get(random.nextInt(population.size()));
            Tour t2 = population.get(random.nextInt(population.size()));
            Tour newTour = t1.recombination(recombinationType, t2);
            toAdd.add(newTour);
        }
        //System.out.println("recombination counter: "+ ct);
        population.addAll(toAdd);
        pq.addAll(toAdd);
    }

    private void mutation(int size){
        List<Tour> toAdd = new ArrayList<>();

        for (int i = 0; i < size * mutationRate; i++) {
            Tour t1 = population.get(random.nextInt(population.size()));
            Tour newTour = t1.mutation(mutationType);
            toAdd.add(newTour);
        }
        //System.out.println("mutation counter: " + ct);
        population.addAll(toAdd);
        pq.addAll(toAdd);
    }

    /**
     * Tournament Selection
     */
    private void survivalOfTheFittest(){
        Set<Tour> removalSet = new HashSet<>();
        List<Tour> checkList = new ArrayList<>(population);
        int halfSize = population.size() / 2;
        //System.out.println("halfSize: " + halfSize);
        for (int i = 0; i < halfSize; i++) {
            int index1 = random.nextInt(checkList.size());
            Tour t1 = checkList.get(index1);
            checkList.remove(index1);

            int index2 = random.nextInt(checkList.size());
            Tour t2 = checkList.get(index2);
            checkList.remove(index2);

            if (t1.getFitness() > t2.getFitness()) {
                removalSet.add(t2);
            } else {
                removalSet.add(t1);
            }
        }

        population.removeAll(removalSet);
        pq.removeAll(removalSet);
        //System.out.println("population size after selection: "+ population.size());
    }

    public static void main(String[] args) {
        Graph g = CompleteGraphGenerator.generate("g", 5, 10,20);
        GeneticAlgorithms ga = new GeneticAlgorithms(g, 3,0,0);
        System.out.println(ga.pq.poll());
        System.out.println(ga.pq.poll());
        System.out.println(ga.pq.poll());
    }

}
