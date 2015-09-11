package scmAlgorithm.treeScorer;

import epos.model.tree.Tree;
import gnu.trove.set.hash.THashSet;
import scmAlgorithm.treeSelector.TreePair;

import java.util.Map;
import java.util.Set;

/**
 * Created by fleisch on 16.06.15.
 */
public class BackboneCladeNumberScorer extends TreeScorer<BackboneCladeNumberScorer> {
    // for this score i use the resolution as a ty breaker. because i can!
    public BackboneCladeNumberScorer(TreeScorer.ConsensusMethods method) {
        super(method);
    }

    protected BackboneCladeNumberScorer(ConsensusMethods method, Map<Tree, THashSet<String>> treeToTaxa) {
        super(method, treeToTaxa);
    }

    public BackboneCladeNumberScorer(ConsensusMethods method, boolean cache, boolean syncedCache) {
        super(method, cache, syncedCache);
    }

    @Override
    protected BackboneCladeNumberScorer newInstance(ConsensusMethods method, Map<Tree, THashSet<String>> treeToTaxa) {
        return new BackboneCladeNumberScorer(method,treeToTaxa);
    }

    //OK
    @Override
    public double scoreTreePair(TreePair pair) {
        Set<String> common =  calculateCommonLeafes(pair.t1, pair.t2);
        pair.setCommonLeafes(common);
        if (common.size() < 3)
            return Double.NEGATIVE_INFINITY;
        //todo prune only do not caclulate comple consensus
        pair.calculateConsensus(getConsensusAlgorithm());
        return (pair.getNumOfBackboneVerticesT1() - pair.getNumOfBackboneTaxa()) + (pair.getNumOfBackboneVerticesT2() - pair.getNumOfBackboneTaxa());
    }



}
