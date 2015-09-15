package scmAlgorithm.treeScorer;

import org.apache.log4j.Logger;
import scmAlgorithm.treeSelector.TreePair;

import java.util.Set;

/**
 * Created by fleisch on 15.06.15.
 */
public class CollisionNumberScorer extends TreeScorer<CollisionNumberScorer> {
    public CollisionNumberScorer(TreeScorer.ConsensusMethods method) {
        super(method);
    }

    public CollisionNumberScorer(ConsensusMethods method, Logger log, boolean cache, boolean syncedCache) {
        super(method,log, cache, syncedCache);
    }

    // GOOOD
    @Override
    public double scoreTreePair(TreePair pair) {
        Set<String> common =  calculateCommonLeafes(pair.t1, pair.t2);
        pair.setCommonLeafes(common);
        if (common.size() < 3)
            return Double.NEGATIVE_INFINITY;

        pair.pruneToCommonLeafes();
        return (- pair.getNumOfCollisions());
    }
}
