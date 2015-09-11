package scmAlgorithm.treeScorer;

import epos.model.tree.Tree;
import gnu.trove.set.hash.THashSet;
import scmAlgorithm.treeSelector.TreePair;

import java.util.Map;
import java.util.Set;

/**
 * Created by fleisch on 16.06.15.
 */
public class CollisionPointMultiCollissionTieBreakScorer extends TreeScorer<CollisionPointMultiCollissionTieBreakScorer> {
    public CollisionPointMultiCollissionTieBreakScorer(ConsensusMethods method) {
        super(method);
    }

    protected CollisionPointMultiCollissionTieBreakScorer(ConsensusMethods method, Map<Tree, THashSet<String>> treeToTaxa) {
        super(method, treeToTaxa);
    }

    public CollisionPointMultiCollissionTieBreakScorer(ConsensusMethods method, boolean cache, boolean syncedCache) {
        super(method, cache, syncedCache);
    }

    @Override
    protected CollisionPointMultiCollissionTieBreakScorer newInstance(ConsensusMethods method, Map<Tree, THashSet<String>> treeToTaxa) {
        return new CollisionPointMultiCollissionTieBreakScorer(method,treeToTaxa);
    }

    @Override
    public double scoreTreePair(TreePair pair) {
        Set<String> common = calculateCommonLeafes(pair.t1, pair.t2);
        pair.setCommonLeafes(common);
        if (common.size() < 3)
            return Double.NEGATIVE_INFINITY;

        pair.pruneToCommonLeafes();
        return (-pair.getNumOfCollisionPointsMultiTieBreak());
    }
}