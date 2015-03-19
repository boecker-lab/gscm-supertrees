package scmAlgorithm.treeSelector;

import epos.model.tree.Tree;
import scmAlgorithm.treeScorer.TreeScorer;

/**
 * Created by fleisch on 06.02.15.
 */
public abstract class TreeSelector {
    public final TreeScorer scorer;

    protected TreeSelector(TreeScorer scorer) {
        this.scorer = scorer;
    }

    public abstract TreePair pollTreePair();
    public abstract boolean addTree(Tree tree);

}
