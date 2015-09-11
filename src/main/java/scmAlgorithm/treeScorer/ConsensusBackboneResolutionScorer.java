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
public class ConsensusBackboneResolutionScorer extends TreeScorer<ConsensusBackboneResolutionScorer> {

    public ConsensusBackboneResolutionScorer(TreeScorer.ConsensusMethods method) {
        super(method);
    }

    protected ConsensusBackboneResolutionScorer(ConsensusMethods method, Map<Tree, THashSet<String>> treeToTaxa) {
        super(method, treeToTaxa);
    }

    public ConsensusBackboneResolutionScorer(ConsensusMethods method, boolean cache, boolean syncedCache) {
        super(method, cache, syncedCache);
    }

    @Override
    protected ConsensusBackboneResolutionScorer newInstance(ConsensusMethods method, Map<Tree, THashSet<String>> treeToTaxa) {
        return new ConsensusBackboneResolutionScorer(method,treeToTaxa);
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
