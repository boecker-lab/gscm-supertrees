package scmAlgorithm.treeSelector;

import java.util.Set;

/**
 * Created by fleisch on 06.02.15.
 */
public class OverlapScorer extends TreeScorer<OverlapScorer> {
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
