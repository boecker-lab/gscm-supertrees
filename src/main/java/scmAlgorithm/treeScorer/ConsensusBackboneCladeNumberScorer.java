package scmAlgorithm.treeScorer;

import epos.model.tree.Tree;
import gnu.trove.set.hash.THashSet;
import scmAlgorithm.treeSelector.TreePair;

import java.util.Map;
import java.util.Set;

/**
 * Created by fleisch on 01.04.15.
 */
public class ConsensusBackboneCladeNumberScorer extends TreeScorer<ConsensusBackboneCladeNumberScorer> {
    // for this score i use the resolution as a ty breaker. because i can!
    public ConsensusBackboneCladeNumberScorer(TreeScorer.ConsensusMethods method) {
        super(method);
    }

    protected ConsensusBackboneCladeNumberScorer(ConsensusMethods method, Map<Tree, THashSet<String>> treeToTaxa) {
        super(method, treeToTaxa);
    }

    public ConsensusBackboneCladeNumberScorer(ConsensusMethods method, boolean cache, boolean syncedCache) {
        super(method, cache, syncedCache);
    }

    @Override
    protected ConsensusBackboneCladeNumberScorer newInstance(ConsensusMethods method, Map<Tree, THashSet<String>> treeToTaxa) {
        return new ConsensusBackboneCladeNumberScorer(method,treeToTaxa);
    }

    @Override
    public double scoreTreePair(TreePair pair) {
        Set<String> common =  calculateCommonLeafes(pair.t1, pair.t2);
        pair.setCommonLeafes(common);
        if (common.size() < 3)
            return Double.NEGATIVE_INFINITY;

        pair.calculateConsensus(getConsensusAlgorithm());
//        return ((pair.getNumOfConsensusBackboneVertices() - pair.getNumOfBackboneTaxa()) * 100) + TreeUtilsBasic.calculateTreeResolution(pair.getNumOfBackboneTaxa(), pair.getNumOfBackboneTaxa()) ;
        return pair.getNumOfConsensusBackboneVertices() - pair.getNumOfBackboneTaxa();
    }
}
