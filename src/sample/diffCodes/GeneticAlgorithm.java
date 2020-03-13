package sample.diffCodes;

import java.awt.*;

@SuppressWarnings("BooleanMethodIsAlwaysInverted")
public class GeneticAlgorithm {

    private int populationSize;
    private double mutationRate;
    private double crossoverRate;
    private int elitismCount;
    private int effectiveGenerationOffset;
    private int currentEffectiveGenerationCounter = 0;
    private double currentEffectiveGenerationFitness = -1.0;

    public GeneticAlgorithm(int populationSize,
                            double mutationRate,
                            double crossoverRate,
                            int elitismCount,
                            int effectiveGenerationOffset) {
        this.populationSize = populationSize;
        this.mutationRate = mutationRate;
        this.crossoverRate = crossoverRate;
        this.elitismCount = elitismCount;
        this.effectiveGenerationOffset = effectiveGenerationOffset;
    }

    public CodesPopulation initPopulation(int singleCodeLength, int codesCount) {
        return new CodesPopulation(populationSize, singleCodeLength, codesCount);
    }

    public CodeIndividual selectParent(CodesPopulation population) {
        CodeIndividual[] individuals = population.getCodes();

        double populationFitness = population.getPopulationFitness();
        double rouletteStopPosition = populationFitness * Math.random();

        double roulettePosition = 0.0;

        for (CodeIndividual individual : individuals) {
            roulettePosition += individual.getFitness();

            if (roulettePosition >= rouletteStopPosition) {
                return individual;
            }
        }

        return individuals[individuals.length - 1];
    }

    public void evaluate(CodesPopulation population) {
        double populationFitness = 0.0;
        for (int i = 0; i < population.getSize(); i++) {
            CodeIndividual codeIndividual = population.getCode(i);
            double fitness = calculateFitness(codeIndividual);
            codeIndividual.setFitness(fitness);
            populationFitness += fitness;
        }
        population.setPopulationFitness(populationFitness);
    }

    public boolean willTerminate(CodesPopulation population) {
        // Ընտրվում է պոպուլյացիայում առավելագույն համապատասխանության
        // արժեքով ինդիվիդուալը
        CodeIndividual fittest = population.getFittest(0);
        double fitness = fittest.getFitness();

        // Ստուգվում է արդյոք աճել է առավելագույն համապատասխանության
        // գործակցի արժեքը գեներացիայի մեկ ցիկլի ընթացքում
        if (currentEffectiveGenerationFitness < fitness) {
            // Եթե բարձրացել է, ապա հիշվում է նոր արժեքը պահպանվում է
            currentEffectiveGenerationFitness = fitness;
            // և եֆեկտիվ գեներացիայի հաշվիչին տրվում է իր նախնական արժեքը
            currentEffectiveGenerationCounter = effectiveGenerationOffset;
            return false;
        } else {
            // եթե չկա համապատասխանության գործակցի բարձրացում,
            // ապա եֆեկտիվության հավիչի արժեքը նվազում է մեկով
            currentEffectiveGenerationCounter--;
            // ստուգվում է արդյոք եֆեկտիվության հավիչի արժեքը հավասարվել է
            // 0-ի
            if (currentEffectiveGenerationCounter == 0) {
                Toolkit.getDefaultToolkit().beep();
            }
            // 0-ի հավասարվելու դեպքում ֆունկցիան վերադարձնում է "true",
            // որը դադարեցնում է գենետիկ ալգորիթմի աշխատանքը
            return currentEffectiveGenerationCounter == 0;
        }
    }

    public CodesPopulation crossover(CodesPopulation population) {
        // Ստեղծում է նոր, դատարկ պոպուլյացիա, որի մեջ ավելացվելու են խաչասերման
        // արդյունքում ստեղծված ինդիվիդուալները
        CodesPopulation newPopulation = new CodesPopulation(population.getSize());

        // Կատարվում է իտերացիա ըստ ներկա պոպուլյացիայի ինդիվիդուալների
        for (int firstParentIndex = 0; firstParentIndex < population.getSize(); firstParentIndex++) {
            // Ընտրվում է առավելագույն համապատասխանության գործակցով ինդիվիդուալը
            CodeIndividual firstParent = population.getFittest(firstParentIndex);

            // Կատարվում է Ստուգում ըստ էլիտիզմի համարի և խաչասերման գործակցի
            if (firstParentIndex >= elitismCount && crossoverRate > Math.random()) {
                // Ընտրվում է երկրորդ ինդիվիդուալը
                CodeIndividual secondParent = selectParent(population);

                // Կատարվում է ընտրված ինդիվիդուալների խաչասերում
                CodeIndividual childIndividual = cross(firstParent, secondParent);

                // Խաչասերման արդյունքում ստացված ինդիվիդուալը ավելացվում է
                // նոր ստեղծված պոպուլյացիային
                newPopulation.setCode(firstParentIndex, childIndividual);
            } else {
                // Էլիտիզմի դրական ստուգման դեպքում կամ խաչասերման գործակցի բացասական
                // ստուգման դեպքում ինդիվիդուալը ավելացվում է նոր պոպուլյացիային առանց
                // որևէ փոփոխության
                newPopulation.setCode(firstParentIndex, firstParent);
            }
        }
        // վերադարծնում է նոր ստեղծված պոպուլյացիան
        return newPopulation;
    }

    private CodeIndividual cross(CodeIndividual firstParent, CodeIndividual secondParent) {
        // պահպանվում են առաջին և երկրորդ ինդիվիդուալների համապատասխանության գործակիցները
        double firstFitness = firstParent.getFitness();
        double secondFitness = secondParent.getFitness();

        // Ստեղծվում է դատարկ նոր ինդիվիդուալ
        CodeIndividual child =
                new CodeIndividual(firstParent.getSingleCodeLength(), firstParent.getCodesCount());

        // Կատարվում է իտերացիա ըստ ինդիվիդուալում եղած կոդերի քանակի
        for (int codeIndex = 0; codeIndex < child.getTotalLength(); codeIndex++) {
            // Առաջին և երկրորդ ինդիվիդուալների համապատասխանության գործակիցները
            // բազմապատկվում են [0;1) միջակայքից պատահական թվով, որը ապահովում է
            // մեխանիզմ, ըստ որի ավելի շատ բիտեր ընտրվում են բարձր
            // համապատասխանության գործակցով ինդիվիդուալից
            double firstRand = firstFitness * Math.random();
            double secondRand = secondFitness * Math.random();
            if (firstRand >= secondRand) {
                child.setCode(codeIndex, firstParent.getCode(codeIndex));
            } else {
                child.setCode(codeIndex, secondParent.getCode(codeIndex));
            }
        }

        // Վերադարձնում է խաչասերման արդյունքում ստեղծված ինդիվիդուալը
        return child;
    }

    public CodesPopulation mutate(CodesPopulation population) {
        // Ստեղծում է նոր, դատարկ պոպուլյացիա, որի մեջ ավելացվելու են մուտացիայի
        // արդյունքում ստեղծված ինդիվիդուալները
        CodesPopulation newPopulation = new CodesPopulation(population.getSize());

        // Կատարվում է իտերացիա ըստ ներկա պոպուլյացիայի ինդիվիդուալների
        for (int populationIndex = 0; populationIndex < population.getSize(); populationIndex++) {

            // Ընտրվում է առավելագույն համապատասխանության գործակցով ինդիվիդուալը
            CodeIndividual individual = population.getFittest(populationIndex);

            // Ստուգվում է էլիտիզմի համարը
            if (populationIndex >= elitismCount) {

                // Կատարվում է իտերացիա ըստ ինդիվիդուալում եղած կոդերի քանակի
                for (int individualIndex = 0; individualIndex < individual.getTotalLength(); individualIndex++) {
                    // Կատարվում է ստուգում ըստ մուտացիայի գործակցի
                    if (mutationRate > Math.random()) {
                        // Մուտացվում է ինդիվիդուալի կոդերի զանգվածի մեկ տարրը,
                        // 1-ը փոխակերպվում է -1-ի և հակառակը
                        double newGene = 1.0;
                        if (individual.getCode(individualIndex) == 1.0) {
                            newGene = -1.0;
                        }
                        // Մուտացված բիտը ավելացվում է ինդիվիդուալի կոդերի զանգվածին
                        individual.setCode(individualIndex, newGene);

                        // Մուտացված ինդիվիդուալը ավելացվում է նոր պոպուլյացիային
                        newPopulation.setCode(populationIndex, individual);
                    } else {
                        // Ինդիվիդուալը ավելացվում է նոր պոպուլյացիային առանց փոփոխության
                        newPopulation.setCode(populationIndex, individual);
                    }
                }
            } else {
                // Ինդիվիդուալը ավելացվում է նոր պոպուլյացիային առանց փոփոխության
                newPopulation.setCode(populationIndex, individual);
            }

        }
        // վերադարձնում է մուտացված ինդիվիդուալներով պոպուլյացիան
        return newPopulation;
    }

    private double calculateFitness(CodeIndividual individual) {

        double overallFitness = 0.0;

        // Կատարվում է իտերացիա ըստ ինդիվիդուալի կոդերի զանգվածի տարրերի
        for (int sourceNumber = 0; sourceNumber < individual.getCodesCount(); sourceNumber++) {

            // Ընտրվում է հերթական 1024 բիտը ինդիվիդուալի կոդերի զանգվածից
            double[] source = individual.getSingleCodeChunk(sourceNumber);

            double sourceFitness = 0.0;

            // Կատարվում է իտերացիա ըստ ինդիվիդուալի զանվածի տարրերի
            for (int sampleNumber = 0; sampleNumber < individual.getCodesCount(); sampleNumber++) {

                if (sampleNumber != sourceNumber) {
                    double[] sample = individual.getSingleCodeChunk(sampleNumber);
                    // Կատարվում է կորելացիա ընտրված երկու կոդերի միջև
                    double coefficient = Correlation.coefficient(source, sample);
                    double corrValue = Correlation.value(0, source.length - 1, source, sample);

                    sourceFitness += (1 - corrValue / coefficient) / individual.getCodesCount();
                }
            }

            // համապատասխանության գործակցի միջին արժեքը ինդիվիդուալի համար
            overallFitness += sourceFitness;
        }

        // համապատասխանության գործակցի միջին արժեքը պոպուլյացիայի համար
        overallFitness = overallFitness / individual.getCodesCount();

        return overallFitness;
    }
}
