package scmAlgorithm.treeScorer;

import epos.model.tree.Tree;
import gnu.trove.set.hash.THashSet;
import scmAlgorithm.treeSelector.TreePair;

import java.util.Map;
import java.util.Set;

/**
 * Created by fleisch on 22.06.15.
 */
public class UniqueTaxaNumberScorer extends TreeScorer<UniqueTaxaNumberScorer>  {
    public UniqueTaxaNumberScorer(TreeScorer.ConsensusMethods method) {
        super(method);
    }

    protected UniqueTaxaNumberScorer(ConsensusMethods method, Map<Tree, THashSet<String>> treeToTaxa) {
        super(method, treeToTaxa);
    }

    public UniqueTaxaNumberScorer(ConsensusMethods method, boolean cache, boolean syncedCache) {
        super(method, cache, syncedCache);
    }

    @Override
    protected UniqueTaxaNumberScorer newInstance(ConsensusMethods method, Map<Tree, THashSet<String>> treeToTaxa) {
        return new UniqueTaxaNumberScorer(method,treeToTaxa);
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
