package scmAlgorithm.treeScorer;

import epos.model.tree.Tree;
import scmAlgorithm.treeSelector.TreePair;

/**
 * Created by fleisch on 06.02.15.
 */
public interface TreeScorer {

    public double getPairwiseScore(Tree tree1, Tree tree2);
    public TreePair getScoredTreePair(Tree tree1, Tree tree2);

    public void clear();
}
