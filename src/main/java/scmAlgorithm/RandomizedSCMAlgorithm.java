package scmAlgorithm;

import epos.model.tree.Tree;
import scmAlgorithm.treeScorer.TreeScorer;
import scmAlgorithm.treeSelector.GreedyTreeSelector;
import scmAlgorithm.treeSelector.RandomizedGreedyTreeSelector;
import scmAlgorithm.treeSelector.TreePair;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by fleisch on 24.03.15.
 */
public class RandomizedSCMAlgorithm extends AbstractSCMAlgorithm implements RandomizedSCMCalculation {
    private final Tree[] inputTrees;
    private GreedySCMAlgorithm nonRandomResult;
    private final int iterations;

    public RandomizedSCMAlgorithm(TreeScorer scorer, Tree... trees) {
        super(new RandomizedGreedyTreeSelector(scorer,false,trees));
        this.inputTrees =trees;
        nonRandomResult =  new GreedySCMAlgorithm(new GreedyTreeSelector(selector.scorer,trees));
        iterations = trees.length * trees.length;
    }

    public RandomizedSCMAlgorithm(TreeScorer scorer, int numberOfIterations, Tree... trees) {
        super(new RandomizedGreedyTreeSelector(scorer,false,trees));
        this.inputTrees =trees;
        nonRandomResult =  new GreedySCMAlgorithm(new GreedyTreeSelector(scorer,trees));
        iterations =  numberOfIterations;
    }

    @Override
    protected List<TreePair> calculateSuperTrees() {
        List<TreePair> superTrees =  new ArrayList<>(iterations + 1);
        superTrees.add(nonRandomResult.calculateSuperTree());
        superTrees.addAll(calculateConsensusRandomized(selector,inputTrees,iterations));
        return superTrees;
    }
}
