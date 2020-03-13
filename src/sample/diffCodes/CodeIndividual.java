package sample.diffCodes;

import java.util.Arrays;

public class CodeIndividual {
    // Կոդերի հաջորդականություններ կազմված 1.0 և -1.0-երից
    private double[] codes;

    // Կոդերի ընդհանուր երկարություն,
    // C/A կոդերի համար հավասար է 32*1024
    private int totalLength;

    // Պարունակող կոդերի քանակը
    // C/A կոդերի համար 32
    private int codesCount;

    // Միավոր կոդի երկարությունը
    // C/A կոդի համար 1024
    private int singleCodeLength;

    // Համապատասխանության գործակից
    private double fitness = -1.0;

    public CodeIndividual(int singleCodeLength, int codesCount) {
        this.singleCodeLength = singleCodeLength;
        this.codesCount = codesCount;
        this.totalLength = singleCodeLength * codesCount;
        this.codes = new double[totalLength];
        for (int i = 0; i < codes.length; i++) {
            double value = Math.random() > 0.5 ? 1.0 :-1.0;
            codes[i] = value;
        }
    }

    public void setFitness(double fitness) {
        this.fitness = fitness;
    }

    public double getFitness() {
        return fitness;
    }

    public int getTotalLength() {
        return totalLength;
    }

    public int getCodesCount() {
        return codesCount;
    }

    public double[] getCodes() {
        return codes;
    }

    public int getSingleCodeLength() {
        return singleCodeLength;
    }

    public void setCode(int offset, double value){
        codes[offset] = value;
    }

    public double getCode(int offset){
        return codes[offset];
    }

    public double[] getSingleCodeChunk(int codeNumber){
        int from = codeNumber*singleCodeLength;
        int to = codeNumber*singleCodeLength + singleCodeLength;
        return Arrays.copyOfRange(codes,from,to);
    }

    public void setSingleCode(int codeNumber,int offset,double value){
        setCode(codeNumber * singleCodeLength + offset,value);
    }

    public double getSingleCode(int codeNumber,int offset){
        return codes[codeNumber * singleCodeLength + offset];
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < totalLength; i++) {
            if (i%singleCodeLength == 0){
                stringBuilder.append(" | ");
            }
            stringBuilder.append(codes[i]+", ");
        }
        return stringBuilder.toString();
    }
}
