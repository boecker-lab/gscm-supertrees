package scmAlgorithm.treeScorer;

import org.apache.log4j.Logger;
import scmAlgorithm.treeSelector.TreePair;

import java.util.Set;

/**
 * Created by fleisch on 22.06.15.
 */
public class UniqueTaxaRateScorer extends TreeScorer<UniqueTaxaRateScorer> {

    public UniqueTaxaRateScorer(ConsensusMethods method) {
        super(method);
    }

    public UniqueTaxaRateScorer(ConsensusMethods method, Logger log, boolean cache, boolean syncedCache) {
        super(method, log, cache, syncedCache);
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