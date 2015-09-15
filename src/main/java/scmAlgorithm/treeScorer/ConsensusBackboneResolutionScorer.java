package scmAlgorithm.treeScorer;

import epos.model.tree.treetools.TreeUtilsBasic;
import org.apache.log4j.Logger;
import scmAlgorithm.treeSelector.TreePair;

import java.util.Set;

/**
 * Created by fleisch on 31.03.15.
 */
public class ConsensusBackboneResolutionScorer extends TreeScorer<ConsensusBackboneResolutionScorer> {

    public ConsensusBackboneResolutionScorer(TreeScorer.ConsensusMethods method) {
        super(method);
    }

    public ConsensusBackboneResolutionScorer(ConsensusMethods method, Logger log, boolean cache, boolean syncedCache) {
        super(method,log, cache, syncedCache);
    }

    @Override
    public double scoreTreePair(TreePair pair) {
        Set<String> common =  calculateCommonLeafes(pair.t1, pair.t2);
        pair.setCommonLeafes(common);
        if (common.size() < 3)
            return Double.NEGATIVE_INFINITY;

        pair.calculateConsensus(getConsensusAlgorithm());
        return TreeUtilsBasic.calculateTreeResolution(pair.getNumOfBackboneTaxa(), pair.getNumOfConsensusBackboneVertices());
    }
}
