package sample.toolkit.polynomial.base_paralle;

import com.sun.istack.internal.Nullable;
import sample.toolkit.polynomial.PolynomialState;
import sample.toolkit.polynomial.polynomial_processor.Lfsr;

import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public abstract class ParallelLfsr {

    // ThreadPool for parallel generation
    protected ThreadPoolExecutor executor = new ThreadPoolExecutor(5,
            5,
            100,
            TimeUnit.MILLISECONDS,
            new ArrayBlockingQueue<Runnable>(100000, false) {
            }, runnable -> {
        final Thread thread = new Thread(runnable);
        thread.setDaemon(false);
        return thread;
    });

    protected boolean skipParallelBits;

    protected volatile boolean isFinished = false;

    protected int[] output;

    public ParallelLfsr() {
        this.skipParallelBits = false;
    }

    public ParallelLfsr(boolean skipParallelBits) {
        this.skipParallelBits = skipParallelBits;
    }

    public int parCount = 0;

    protected Consumer<int[]> onCompleteListener;
    @Nullable
    protected BiConsumer<Integer, Integer> onEachProcessValueGeneration;

    @Nullable
    protected BiConsumer<Integer, PolynomialState> onEachProcessStateGeneration;

    public void generate(int[] initialState,
                         int[] exOrIndexes,
                         int step,
                         Consumer<int[]> onCompleteListener) {
        generate(initialState, exOrIndexes, new int[]{initialState.length}, step, onCompleteListener, null, null);
    }

    public void generate(int[] initialState,
                         int[] exOrIndexes,
                         int[] outputRegisters,
                         int step,
                         Consumer<int[]> onCompleteListener) {
        generate(initialState, exOrIndexes, outputRegisters, step, onCompleteListener, null, null);
    }

    public void generate(int[] initialState,
                         int[] exOrIndexes,
                         int[] outputRegisters,
                         int step,
                         Consumer<int[]> onCompleteListener,
                         BiConsumer<Integer, Integer> onEachProcessValueGeneration) {
        generate(initialState, exOrIndexes, outputRegisters, step, onCompleteListener, onEachProcessValueGeneration, null);
    }

    public void generate(int[] initialState,
                         int[] exOrIndexes,
                         int[] outputRegisters,
                         int step,
                         Consumer<int[]> onCompleteListener,
                         BiConsumer<Integer, Integer> onEachProcessValueGeneration,
                         BiConsumer<Integer, PolynomialState> onEachProcessStateGeneration) {
        this.onCompleteListener = onCompleteListener;
        this.onEachProcessValueGeneration = onEachProcessValueGeneration;
        this.onEachProcessStateGeneration = onEachProcessStateGeneration;
        // Create the initial state
        final PolynomialState state = new PolynomialState(initialState);
        // The maximum period of FibonacciLfsr, period = 2^registersCount - 1
        int maxRun = (int) Math.pow(2, state.getValues().length) - 1;
        // Initialize the output array with length maxRun
        output = new int[maxRun];
        isFinished = false;
        // Start the generation
        generate(state, exOrIndexes, outputRegisters, step, output);
    }

    public int[] getOutput() {
        return output;
    }

    public boolean isFinished() {
        return isFinished;
    }

    // generate() մեթոդը կատարում է զուգահեռացման մեկ
    // փուլի գեներացիա։
    // state - պարունակում է ռեգիստրի վիճակի մասին ինֆորմացիա,
    // օրինակ ռեգիստրի թաբերի արժեքները
    // feedbackPositions - հետադարձ կապի համարները
    // outputRegisters - ելքային թաբերի համարները
    // step - ռեգիստրի գեներացիայի քայլի համարը,
    // որտեղ զրայական քայլի դեպքում ռեգիստրը կազմող թաբերի
    // արժեքները հավասար են 0-ի
    // output - ելքային հաջորդականությունների զանգված
    protected void generate(PolynomialState state,
                            final int[] feedbackPositions,
                            final int[] outputRegisters,
                            int step,
                            final int[] output) {

        // Որոշվում է ռեգիստրի թաբերի քանակը
        final int tabsCount = state.getValues().length;
        // Որոշվում է ռեգիստրի կրկնման ցիկլի երկարությունը (2^n -1)
        final int maxRun = (int) Math.pow(2, tabsCount) - 1;
        // Հայտարարվում է FibonacciLfsr դասը տրված հետադարձ կապերի դիրքերի արժեքներով,
        // տաբերի քանակով և ելքային թաբերի դիրքերի համարներով
        final Lfsr lfsr = createLfsr(feedbackPositions,outputRegisters,tabsCount);
        // Տրվում է ռեգիստրի թաբերի արժեքները
        lfsr.setState(state);
        // states փոփոխականի մեջ պահվում են ռեգիստրի գեներացիայի քայլերի այն թաբերի արժեքները,
        // որոնք օգտագործվելու են զուգահեռացման բանաձևում
        final Map<Integer, PolynomialState> states = calculateParallelSteps(feedbackPositions, tabsCount, step);
        // Կատարվում է իտերացիա ըստ տրաված գեներացիայի քայլի
        for (int generationStep = step; generationStep < step + tabsCount; generationStep++) {
            // Գեներացիայի ավարտի ստուգում
            if (generationStep == maxRun) {
                isFinished = true;
                if (onCompleteListener!=null)
                onCompleteListener.accept(output);
                System.out.println("Parallel generations count= " + parCount);
                break;
            }
            // Պահվում է թաբերի արժեքները,
            // եթե ներկա գեներացիայի քայլը օգտագործվելու է զուգահեռացման բանաձևում
            if (states.containsKey(generationStep)) {
                states.put(generationStep, lfsr.captureState());
            }

            if (onEachProcessStateGeneration != null) {
                onEachProcessStateGeneration.accept(generationStep, lfsr.captureState());
            }
            // Կատարվում է գեներացիայի քայլին համապատասխան ելքային
            // բիտի գեներացիա
            final int outputBit = lfsr.process();
            // Պահվում է գեներացված բիտը
            output[generationStep] = outputBit;
            if (onEachProcessValueGeneration != null) {
                onEachProcessValueGeneration.accept(generationStep, outputBit);
            }

            // Զուգահեռացման կետի ստուգում
            if (generationStep == Collections.max(states.keySet())) {
                // Զուգահեռացման բանաձևից ստացված թաբերի արժեքների
                // գեներացիայի
                final int parallelStep = step + tabsCount;
                // Գեներացիայի ավարտի ստուգում
                if (parallelStep <= maxRun) {
                    // Զուգահեռացման բանաձևին մասնակցող գեներացիայի
                    // քայլերին համապատասխան
                    // թաբերի արժեքների գումար ըստ մոդուլ երկուսի
                    PolynomialState sumState = states.values().iterator().next();
                    for (PolynomialState nextState : states.values()) {
                        if (sumState != nextState) {
                            sumState = sumState.exOr(nextState);
                        }
                    }
                    final PolynomialState finalState = sumState;

                    Runnable nextGeneration = () -> {
                        // Pass the calculated state, feedbackIndices, parallelStep, output
                        generate(finalState, feedbackPositions, outputRegisters, parallelStep, output);
                    };
                    // Post the parallel generation to the new Java Thread
                    if (skipParallelBits) {
                        nextGeneration.run();
                    } else {
                        parCount++;
                        // Զուգահեռ սկսում է նոր զուգահեռացաման փուլ
                        executor.execute(nextGeneration);
                    }

                    // we break parallel bits generation, mostly it's needed for correlation
                    if (skipParallelBits) {
                        break;
                    }
                }
            }
        }
    }


    protected abstract Lfsr createLfsr(final int[] exOrIndexes,
                                       final int[] outputRegisters,
                                       final int registersCount);

    protected abstract Map<Integer, PolynomialState> calculateParallelSteps(int[] feedbackPositions, Integer tabsCount, int step);
}
