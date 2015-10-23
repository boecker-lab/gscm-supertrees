package scmAlgorithm;

import epos.model.tree.Tree;
import org.apache.log4j.Logger;
import parallel.DefaultIterationCallable;
import parallel.IterationCallableFactory;
import scmAlgorithm.treeSelector.*;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;

/**
 * Created by fleisch on 24.03.15.
 */
public class RandomizedSCMAlgorithm extends AbstractSCMAlgorithm implements MergedMultipleSCMResults {
    private final Tree[] inputTrees;
    private GreedySCMAlgorithm nonRandomResult;
    private TreeScorer[] scorerArray = null;
    private final int iterations;
    private boolean multipleRandomizedRuns = false;

    public RandomizedSCMAlgorithm(Tree[] trees, TreeScorer... scorer) {
        super(new RandomizedGreedyTreeSelector());
        nonRandomResult = new GreedySCMAlgorithm(new GreedyTreeSelector());
        this.inputTrees = trees;
        this.scorerArray = scorer;
        this.iterations = trees.length * trees.length;
    }

    public RandomizedSCMAlgorithm(int numberOfIterations, Tree[] trees, TreeScorer... scorer) {
        super(new RandomizedGreedyTreeSelector());
        nonRandomResult = new GreedySCMAlgorithm(new GreedyTreeSelector());
        this.inputTrees = trees;
        this.scorerArray = scorer;
        this.iterations = numberOfIterations;
    }

    public RandomizedSCMAlgorithm(boolean multipleRandomizedRuns, int numberOfIterations, Tree[] trees, TreeScorer... scorer) {
        super(new RandomizedGreedyTreeSelector());
        nonRandomResult = new GreedySCMAlgorithm(new GreedyTreeSelector());
        this.inputTrees = trees;
        this.scorerArray = scorer;
        this.iterations = numberOfIterations;
        this.multipleRandomizedRuns = multipleRandomizedRuns;
    }

    public RandomizedSCMAlgorithm(Logger logger, ExecutorService executorService, TreeSelector selector, Tree[] inputTrees, int iterations, TreeScorer... scorer) {
        super(logger, executorService, selector);
        this.inputTrees = inputTrees;
        this.iterations = iterations;
        this.scorerArray = scorer;
    }

    public RandomizedSCMAlgorithm(Logger logger, TreeSelector selector, Tree[] inputTrees, int iterations, TreeScorer... scorer) {
        super(logger, selector);
        this.inputTrees = inputTrees;
        this.iterations = iterations;
        this.scorerArray = scorer;
    }

    public RandomizedSCMAlgorithm(TreeSelector selector, Tree[] inputTrees, int iterations, TreeScorer... scorer) {
        super(selector);
        this.inputTrees = inputTrees;
        this.iterations = iterations;
        this.scorerArray = scorer;
    }

    @Override
    protected List<TreePair> calculateSuperTrees() {
        //todo parallelize
        List<TreePair> superTrees = new ArrayList<>((iterations + 1) * scorerArray.length);

        //some additional, optional non random results with different scorings.
        for (int i = 0; i < scorerArray.length; i++) {
            TreeScorer scorer = scorerArray[i];
            nonRandomResult.selector.setScorer(scorer);
            superTrees.add(nonRandomResult.calculateSuperTree());

            if (multipleRandomizedRuns  ||  i < 1) {
                selector.setScorer(scorer);
                superTrees.addAll(calculateRandomizedConsensusSequencial());
            }
        }
        return superTrees;
    }

    private List<TreePair> calculateRandomizedConsensusSequencial() {
        List<TreePair> superTrees = new ArrayList<>(iterations);
        for (int i = 0; i < iterations; i++) {
            superTrees.add(calculateGreedyConsensus(selector, false));
        }
        //sort supertrees
        return superTrees;
    }

    private List<TreePair> calculateRandomizedConsensusParallel() {
        List<TreePair> superTrees = new ArrayList<>(iterations);
        List<TreeSelector> jobs = new ArrayList<>(iterations);

        for (int i = 0; i < iterations; i++) {
            jobs.add(factory.newTreeSelectorInstance());
            selector.init(inputTrees);
            superTrees.add(calculateGreedyConsensus(selector, false));
        }
        //sort supertrees
        return superTrees;
    }

    class GSCMCallable extends DefaultIterationCallable<RandomizedGreedyTreeSelector, TreePair> {
        final TreeScorer scorer;
        final Tree[] inputTrees;

        public GSCMCallable(List<RandomizedGreedyTreeSelector> jobs, TreeScorer scorer, Tree[] inputTrees) {
            super(jobs);
            this.scorer = scorer;
            this.inputTrees = inputTrees;
        }

        @Override
        public TreePair doJob(RandomizedGreedyTreeSelector selector) {
            selector.setScorer(scorer);
            selector.setInputTrees(inputTrees);
            return calculateGreedyConsensus(selector, false);
        }
    }

    class GSCMCallableFactory implements IterationCallableFactory<GSCMCallable, RandomizedGreedyTreeSelector> {
        final TreeScorer scorer;
        final Tree[] inputTrees;

        public GSCMCallableFactory(TreeScorer scorer, Tree[] inputTrees) {
            this.scorer = scorer;
            this.inputTrees = inputTrees;
        }

        @Override
        public GSCMCallable newIterationCallable(List<RandomizedGreedyTreeSelector> list) {
            return new GSCMCallable(list,scorer,inputTrees);
        }
    }
}
