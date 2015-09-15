package scmAlgorithm.treeScorer;

import org.apache.log4j.Logger;
import scmAlgorithm.treeSelector.TreePair;

import java.util.Set;

/**
 * Created by fleisch on 06.02.15.
 */
public class OverlapScorer extends TreeScorer<OverlapScorer> {

    public OverlapScorer(ConsensusMethods method) {
        super(method);
    }

    public OverlapScorer(ConsensusMethods method, Logger log, boolean cache, boolean syncedCache) {
        super(method, log, cache, syncedCache);
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
