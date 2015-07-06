package scmAlgorithm.treeScorer;

import scmAlgorithm.treeSelector.TreePair;

import java.util.Set;

/**
 * Created by fleisch on 16.06.15.
 */
public class BackboneCladeNumberScorer extends TreeScorer {
    // for this score i use the resolution as a ty breaker. because i can!
    public BackboneCladeNumberScorer(TreeScorer.ConsensusMethods method) {
        super(method);
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
