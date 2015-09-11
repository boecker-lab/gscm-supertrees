package scmAlgorithm.treeScorer;

import epos.model.tree.Tree;
import epos.model.tree.treetools.TreeUtilsBasic;
import gnu.trove.set.hash.THashSet;
import scmAlgorithm.treeSelector.TreePair;

import java.util.Map;
import java.util.Set;

/**
 * Created by fleisch on 31.03.15.
 */
public class ConsensusCladeNumberScorer extends TreeScorer<ConsensusCladeNumberScorer> {

    // for this score i use the resolution as a ty breaker. because i can!
    public ConsensusCladeNumberScorer(ConsensusMethods method) {
        super(method);
    }

    protected ConsensusCladeNumberScorer(ConsensusMethods method, Map<Tree, THashSet<String>> treeToTaxa) {
        super(method, treeToTaxa);
    }

    public ConsensusCladeNumberScorer(ConsensusMethods method, boolean cache, boolean syncedCache) {
        super(method, cache, syncedCache);
    }

    @Override
    protected ConsensusCladeNumberScorer newInstance(ConsensusMethods method, Map<Tree, THashSet<String>> treeToTaxa) {
        return new ConsensusCladeNumberScorer(method,treeToTaxa);
    }

    @Override
    public double scoreTreePair(TreePair pair) {
        Set<String> common =  calculateCommonLeafes(pair.t1, pair.t2);
        pair.setCommonLeafes(common);
        if (common.size() < 3)
            return Double.NEGATIVE_INFINITY;

        pair.calculateConsensus(getConsensusAlgorithm());
//        return ((pair.getNumOfConsensusVertices() - pair.getNumOfConsensusTaxa()) * 100) + TreeUtilsBasic.calculateTreeResolution(pair.getNumOfConsensusTaxa(), pair.getNumOfConsensusVertices()) ;
        return pair.getNumOfConsensusVertices() - pair.getNumOfConsensusTaxa();
    }
}
