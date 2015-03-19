package scmAlgorithm.treeScorer;

import epos.model.tree.Tree;
import scmAlgorithm.treeSelector.TreePair;

import java.util.Set;

/**
 * Created by fleisch on 16.03.15.
 */
public class ResolutionScorer extends TreeScorer {


    public ResolutionScorer(ConsensusMethods method) {
        super(method);
    }

    @Override
    public double scoreTreePair(TreePair pair) {
        Set<String> common =  calculateCommonLeafes(pair.t1, pair.t2);
        pair.setCommonLeafes(common);
        if (common.size() < 3)
            return Double.NEGATIVE_INFINITY;

        Tree c =  pair.getConsensus(getConsensusAlgorithm());
        int numTaxa =  pair.getConsensusNumOfTaxa();
        return ((double)(c.vertexCount() - numTaxa)) / ((double)(numTaxa - 1));
    }
}
