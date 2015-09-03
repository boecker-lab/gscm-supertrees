package scmAlgorithm.treeScorer;

import scmAlgorithm.treeSelector.TreePair;

import java.util.Set;

/**
 * Created by fleisch on 16.06.15.
 */
public class CollisionLostCladesNumberScorer extends TreeScorer {
    public CollisionLostCladesNumberScorer(TreeScorer.ConsensusMethods method) {
        super(method);
    }

    //BAD
    @Override
    public double scoreTreePair(TreePair pair) {
        Set<String> common = calculateCommonLeafes(pair.t1, pair.t2);
        pair.setCommonLeafes(common);
        if (common.size() < 3)
            return Double.NEGATIVE_INFINITY;

        pair.pruneToCommonLeafes();
        return (-pair.getNumOfCollisionDestructedClades());
    }
}