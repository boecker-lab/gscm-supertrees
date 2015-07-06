package scmAlgorithm.treeScorer;

import scmAlgorithm.treeSelector.TreePair;

import java.util.Set;

/**
 * Created by fleisch on 15.06.15.
 */
public class CollisionNumberScorer extends TreeScorer {
    public CollisionNumberScorer(TreeScorer.ConsensusMethods method) {
        super(method);
    }
    // GOOOD
    @Override
    public double scoreTreePair(TreePair pair) {
        Set<String> common =  calculateCommonLeafes(pair.t1, pair.t2);
        pair.setCommonLeafes(common);
        if (common.size() < 3)
            return Double.NEGATIVE_INFINITY;

        pair.calculateConsensus(getConsensusAlgorithm());
        return (- pair.getNumOfCollisions());
    }
}
