package scmAlgorithm.treeScorer;

import scmAlgorithm.treeSelector.TreePair;

import java.util.Set;

/**
 * Created by fleisch on 22.06.15.
 */
public class UniqueTaxaNumberScorer extends TreeScorer  {
    public UniqueTaxaNumberScorer(TreeScorer.ConsensusMethods method) {
        super(method);
    }

    @Override
    public double scoreTreePair(TreePair pair) {
        Set<String> common =  calculateCommonLeafes(pair.t1, pair.t2);
        pair.setCommonLeafes(common);
        if (common.size() < 3)
            return Double.NEGATIVE_INFINITY;

        return -((treeToTaxa.get(pair.t1).size() - common.size()) + (treeToTaxa.get(pair.t2).size() - common.size()));
    }

}
