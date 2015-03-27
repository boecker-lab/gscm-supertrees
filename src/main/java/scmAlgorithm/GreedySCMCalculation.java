package scmAlgorithm;

import epos.model.tree.Tree;
import scmAlgorithm.treeSelector.TreePair;
import scmAlgorithm.treeSelector.TreeSelector;

/**
 * Created by fleisch on 25.03.15.
 */
public interface GreedySCMCalculation {

    default TreePair calculateGreedyConsensus(TreeSelector selector) {
        TreePair superCandidatePair = null;
        TreePair pair;
        while((pair = selector.pollTreePair()) != null){
            Tree superCandidate = pair.getConsensus(selector.scorer.getConsensusAlgorithm());
            selector.addTree(superCandidate);
            superCandidatePair =  pair;
        }
        return superCandidatePair;
    }

}
