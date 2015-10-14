package scmAlgorithm.treeSelector;

import java.util.Set;

/**
 * Created by fleisch on 15.06.15.
 */
public class CollisionPointNumberScorer extends TreeScorer<CollisionPointNumberScorer> {
    //OK
    @Override
    public double scoreTreePair(TreePair pair) {
        Set<String> common =  calculateCommonLeafes(pair.t1, pair.t2);
        pair.setCommonLeafes(common);
        if (common.size() < 3)
            return Double.NEGATIVE_INFINITY;

        pair.pruneToCommonLeafes();
        return (- pair.getNumOfCollisionPoints());
    }
}
