package scmAlgorithm.treeSelector;

import epos.model.tree.treetools.TreeUtilsBasic;

import java.util.Set;

/**
 * Created by fleisch on 16.03.15.
 */
public class ConsensusResolutionScorer extends TreeScorer<ConsensusResolutionScorer> {
    @Override
    public double scoreTreePair(TreePair pair) {
        Set<String> common =  calculateCommonLeafes(pair.t1, pair.t2);
        pair.setCommonLeafes(common);
        if (common.size() < 3)
            return Double.NEGATIVE_INFINITY;

        pair.calculateConsensus();
        return TreeUtilsBasic.calculateTreeResolution(pair.getNumOfConsensusTaxa(), pair.getNumOfConsensusVertices());
    }
}
