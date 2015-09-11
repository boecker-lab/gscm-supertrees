package scmAlgorithm.treeScorer;

import epos.model.tree.Tree;
import gnu.trove.set.hash.THashSet;
import scmAlgorithm.treeSelector.TreePair;

import java.util.Map;
import java.util.Set;

/**
 * Created by fleisch on 22.06.15.
 */
public class UniqueTaxaRateScorer extends TreeScorer<UniqueTaxaRateScorer> {

    public UniqueTaxaRateScorer(TreeScorer.ConsensusMethods method) {
        super(method);
    }

    protected UniqueTaxaRateScorer(ConsensusMethods method, Map<Tree, THashSet<String>> treeToTaxa) {
        super(method, treeToTaxa);
    }

    public UniqueTaxaRateScorer(ConsensusMethods method, boolean cache, boolean syncedCache) {
        super(method, cache, syncedCache);
    }

    @Override
    protected UniqueTaxaRateScorer newInstance(ConsensusMethods method, Map<Tree, THashSet<String>> treeToTaxa) {
        return new UniqueTaxaRateScorer(method,treeToTaxa);
    }

    @Override
    public double scoreTreePair(TreePair pair) {
        Set<String> common = calculateCommonLeafes(pair.t1, pair.t2);
        pair.setCommonLeafes(common);
        if (common.size() < 3)
            return Double.NEGATIVE_INFINITY;

        int tax1 = treeToTaxa.get(pair.t1).size();
        int tax2 = treeToTaxa.get(pair.t2).size();
        return -((tax1 - common.size())/tax1 + (tax2 - common.size())/tax2);
    }
}