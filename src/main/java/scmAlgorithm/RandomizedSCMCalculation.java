package scmAlgorithm;

import epos.model.tree.Tree;
import scmAlgorithm.treeSelector.TreePair;
import scmAlgorithm.treeSelector.TreeSelector;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by fleisch on 25.03.15.
 */
public interface RandomizedSCMCalculation extends GreedySCMCalculation {

    List<TreePair> calculateRandomizedConsensus(TreeSelector selector, Tree[] inputTrees, int iterations);
    /*default List<TreePair> calculateRandomizedConsensus(TreeSelector selector, Tree[] inputTrees, int iterations) {
        List<TreePair> superTrees =  new ArrayList<>(iterations);
        for (int i = 0; i < iterations; i++) {
            selector.init(inputTrees);
            superTrees.add(calculateGreedyConsensus(selector));
        }
        //sort supertrees
        return superTrees;
    }*/
}
