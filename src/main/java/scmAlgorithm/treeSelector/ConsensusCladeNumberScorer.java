package scmAlgorithm.treeSelector;

import java.util.Set;

/**
 * Created by fleisch on 31.03.15.
 */
public class ConsensusCladeNumberScorer extends TreeScorer<ConsensusCladeNumberScorer> {
    // for this score i use the resolution as a ty breaker. because i can!
    @Override
    public double scoreTreePair(TreePair pair) {
        Set<String> common =  calculateCommonLeafes(pair.t1, pair.t2);
        pair.setCommonLeafes(common);
        if (common.size() < 3)
            return Double.NEGATIVE_INFINITY;

        pair.calculateConsensus();
//        return ((pair.getNumOfConsensusVertices() - pair.getNumOfConsensusTaxa()) * 100) + TreeUtilsBasic.calculateTreeResolution(pair.getNumOfConsensusTaxa(), pair.getNumOfConsensusVertices()) ;
        return pair.getNumOfConsensusVertices() - pair.getNumOfConsensusTaxa();
    }
}
