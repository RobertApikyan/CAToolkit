package sample.diffCodes;

public class DiffCodes {

    public static void main(String[] args) {

        int populationSize = 100;

        double mutationRate = 0.05;

        double crossoverRate = 0.95;

        int elitismCount = 1;
        int singleCodeLength = 5;


        int codesCount = 3;

        int effectiveGenerationOffset = 2000;

        int generation = 0;

        GeneticAlgorithm ga = new GeneticAlgorithm(populationSize,mutationRate,crossoverRate,elitismCount, effectiveGenerationOffset);

        CodesPopulation population = ga.initPopulation(singleCodeLength,codesCount);

        ga.evaluate(population);

        while (!ga.willTerminate(population)){

            CodeIndividual fittest = population.getFittest(0);

            System.out.println("fitness= " +fittest.getFitness() + " " + fittest.toString());

            population = ga.crossover(population);

            population = ga.mutate(population);

            ga.evaluate(population);

            generation++;
        }

        System.out.println("Found solution in " + generation + " generationNumber");
        System.out.println("Best solution: " + population.getFittest(0).toString());

    }
}
