package sample.diffCodes;

import java.util.Arrays;

public class CodesPopulation {
    private double populationFitness = -1;
    private int size;
    private CodeIndividual[] codes;

    public CodesPopulation(int size){
        this.size = size;
        codes = new CodeIndividual[size];
    }

    public CodesPopulation(int size, int singleCodeLength, int codesCount){
        this.size = size;
        codes = new CodeIndividual[size];
        for (int i = 0; i < size; i++) {
            codes[i] = new CodeIndividual(singleCodeLength,codesCount);
        }
    }

    public void setCode(int offset, CodeIndividual code){
        codes[offset] = code;
    }

    public CodeIndividual getCode(int offset){
        return codes[offset];
    }

    public void setPopulationFitness(double populationFitness) {
        this.populationFitness = populationFitness;
    }

    public double getPopulationFitness() {
        return populationFitness;
    }

    public CodeIndividual[] getCodes() {
        return codes;
    }

    public CodeIndividual getFittest(int offset){
        Arrays.sort(this.codes, (o1, o2) -> {
            if (o1.getFitness() > o2.getFitness()){
                return -1;
            }else if (o1.getFitness() < o2.getFitness()){
                return 1;
            }
            return 0;
        });

        return codes[offset];
    }

    public int getSize() {
        return size;
    }
}
