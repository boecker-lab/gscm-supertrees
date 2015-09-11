package scmAlgorithm.treeScorer;

import epos.model.tree.Tree;
import gnu.trove.set.hash.THashSet;
import scmAlgorithm.treeSelector.TreePair;

import java.util.Map;
import java.util.Set;

/**
 * Created by fleisch on 06.02.15.
 */
public class OverlapScorer extends TreeScorer<OverlapScorer> {
    public OverlapScorer(ConsensusMethods method) {
        super(method);
    }

    protected OverlapScorer(ConsensusMethods method, Map<Tree, THashSet<String>> treeToTaxa) {
        super(method, treeToTaxa);
    }

    public OverlapScorer(ConsensusMethods method, boolean cache, boolean syncedCache) {
        super(method, cache, syncedCache);
    }

    @Override
    protected OverlapScorer newInstance(ConsensusMethods method, Map<Tree, THashSet<String>> treeToTaxa) {
        return new OverlapScorer(method,treeToTaxa);
    }

    @Override
    public double scoreTreePair(TreePair pair) {
        Set<String> common =  calculateCommonLeafes(pair.t1, pair.t2);
        pair.setCommonLeafes(common);
        if (common.size() < 3)
            return Double.NEGATIVE_INFINITY;
//        return pair.getNumOfBackboneTaxa();
        return pair.getNumOfBackboneTaxa();
    }
}
