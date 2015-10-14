package scmAlgorithm.treeSelector;

import java.util.Set;

/**
 * Created by fleisch on 16.06.15.
 */
public class ConsensusBackboneSizeScorer extends TreeScorer<ConsensusBackboneSizeScorer> {
    @Override
    public double scoreTreePair(TreePair pair) {
        Set<String> common =  calculateCommonLeafes(pair.t1, pair.t2);
        pair.setCommonLeafes(common);
        if (common.size() < 3)
            return Double.NEGATIVE_INFINITY;

        pair.calculateConsensus();
        return pair.getNumOfConsensusBackboneVertices();
    }
}
