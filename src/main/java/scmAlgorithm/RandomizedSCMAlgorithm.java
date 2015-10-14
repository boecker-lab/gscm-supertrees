package scmAlgorithm;

import epos.model.tree.Tree;
import org.apache.log4j.Logger;
import scmAlgorithm.treeSelector.TreeScorer;
import scmAlgorithm.treeSelector.GreedyTreeSelector;
import scmAlgorithm.treeSelector.RandomizedGreedyTreeSelector;
import scmAlgorithm.treeSelector.TreePair;
import scmAlgorithm.treeSelector.TreeSelector;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;

/**
 * Created by fleisch on 24.03.15.
 */
public class RandomizedSCMAlgorithm extends AbstractSCMAlgorithm implements RandomizedSCMCalculation, MultiResultMergeSCMAlgorithm {
    private final Tree[] inputTrees;
    private GreedySCMAlgorithm nonRandomResult;
    private TreeScorer[] scorerArray = null;
    private final int iterations;
    private boolean multipleRandomizedRuns = false;

    public RandomizedSCMAlgorithm(Tree[] trees, TreeScorer... scorer) {
        super(new RandomizedGreedyTreeSelector(scorer[0], false, trees));
        nonRandomResult = new GreedySCMAlgorithm(new GreedyTreeSelector(scorer[0], trees));
        this.inputTrees = trees;
        this.scorerArray = scorer;
        this.iterations = trees.length * trees.length;
    }

    public RandomizedSCMAlgorithm(int numberOfIterations, Tree[] trees, TreeScorer... scorer) {
        super(new RandomizedGreedyTreeSelector(scorer[0], false, trees));
        nonRandomResult = new GreedySCMAlgorithm(new GreedyTreeSelector(scorer[0], trees));
        this.inputTrees = trees;
        this.scorerArray = scorer;
        this.iterations = numberOfIterations;
    }

    public RandomizedSCMAlgorithm(boolean multipleRandomizedRuns, int numberOfIterations, Tree[] trees, TreeScorer... scorer) {
        super(new RandomizedGreedyTreeSelector(scorer[0], false, trees));
        nonRandomResult = new GreedySCMAlgorithm(new GreedyTreeSelector(scorer[0], trees));
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
        List<TreePair> superTrees = new ArrayList<>((iterations + 1) * scorerArray.length);
        superTrees.add(nonRandomResult.calculateSuperTree());
        superTrees.addAll(calculateRandomizedConsensus(selector, inputTrees, iterations));

        //some additional, optional non random results with different scorings.
        if (scorerArray.length > 1) {
            for (int i = 1; i < scorerArray.length; i++) {
                TreeScorer scorer = scorerArray[i];
                nonRandomResult.selector.setScorer(scorer);
                nonRandomResult.selector.init(inputTrees);
                superTrees.add(nonRandomResult.calculateSuperTree());
            }

            if (multipleRandomizedRuns) {
                for (int i = 1; i < scorerArray.length; i++) {
                    TreeScorer scorer = scorerArray[i];
                    selector.setScorer(scorer);
                    superTrees.addAll(calculateRandomizedConsensus(selector, inputTrees, iterations));
                }
            }
        }

        return superTrees;
    }
}
