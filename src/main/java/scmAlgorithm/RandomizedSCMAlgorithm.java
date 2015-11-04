package scmAlgorithm;

import epos.model.tree.Tree;
import parallel.ParallelUtils;
import scmAlgorithm.treeSelector.GreedyTreeSelector;
import scmAlgorithm.treeSelector.RandomizedGreedyTreeSelector;
import scmAlgorithm.treeSelector.TreePair;
import scmAlgorithm.treeSelector.TreeScorer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

/**
 * Created by fleisch on 24.03.15.
 */
public class RandomizedSCMAlgorithm extends AbstractMultipleResultsSCMAlgorithm {
    private int iterations = 0;

    public RandomizedSCMAlgorithm(TreeScorer... scorer) {
        this(null, scorer);
    }

    public RandomizedSCMAlgorithm(Tree[] trees, TreeScorer... scorer) {
        super(trees, scorer);
        iterations = defaultIterations();
    }

    public RandomizedSCMAlgorithm(int numberOfIterations, TreeScorer... scorer) {
        this(numberOfIterations, null, scorer);
    }

    public RandomizedSCMAlgorithm(int numberOfIterations, Tree[] trees, TreeScorer... scorer) {
        super(trees, scorer);
        this.iterations = numberOfIterations;
    }

    private int defaultIterations() {
        return inputTrees.length * inputTrees.length;
    }

    @Override
    protected int numOfJobs() {
        return (iterations + 1) * scorerArray.length;
    }

    protected List<TreePair> calculateSequencial() {
        final GreedyTreeSelector nonRandomResultSelector = new GreedyTreeSelector();
        nonRandomResultSelector.setInputTrees(inputTrees);
        final RandomizedGreedyTreeSelector randomResultSelector = new RandomizedGreedyTreeSelector();
        randomResultSelector.setInputTrees(inputTrees);

        List<TreePair> superTrees = new ArrayList<>();
        for (TreeScorer treeScorer : scorerArray) {
            List<TreePair> scms = new ArrayList<>(iterations + 1);

            nonRandomResultSelector.setScorer(treeScorer);
            scms.add((calculateGreedyConsensus(nonRandomResultSelector, false)));
            randomResultSelector.setScorer(treeScorer);
            for (int i = 0; i < iterations; i++) {
                scms.add(calculateGreedyConsensus(randomResultSelector, false));
            }
            superTrees.addAll(scms);
        }

        return superTrees;
    }

    protected List<TreePair> calculateParallel() {
        List<TreePair> superTrees = new ArrayList<>(numOfJobs());
        List<Future<List<TreePair>>> futurList = new LinkedList<>();

        //todo maybe bucked parallelism
        //calculate random results
        GSCMCallableFactory randomFactory = new GSCMCallableFactory(RandomizedGreedyTreeSelector.getFactory(), inputTrees);
        for (int i = 0; i < iterations; i++) {
            futurList.addAll(
                    ParallelUtils.parallelForEach(executorService, randomFactory, Arrays.asList(scorerArray)));
        }

        //calculate nonRandomResults
        GSCMCallableFactory nonRandomFactory = new GSCMCallableFactory(GreedyTreeSelector.getFactory(), inputTrees);
        futurList.addAll(
                ParallelUtils.parallelForEach(executorService, nonRandomFactory, Arrays.asList(scorerArray)));

        //collect results
        try {
            for (Future<List<TreePair>> future : futurList) {
                superTrees.addAll(future.get());
            }
            return superTrees;
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void setInput(boolean defaultNumOfIterations, Tree... trees) {
        if (defaultNumOfIterations)
            iterations = defaultIterations();
        setInput(trees);
    }

    public void setNumberOfIterations(int iterations) {
        this.iterations = iterations;
    }
}
