package scmAlgorithm.treeSelector;

import java.util.Set;

/**
 * Created by fleisch on 22.06.15.
 */
public class SubsetUnitOverlapScorer extends TreeScorer<SubsetUnitOverlapScorer>  {
    @Override
    public double scoreTreePair(TreePair pair) {
        Set<String> common =  calculateCommonLeafes(pair.t1, pair.t2);
        pair.setCommonLeafes(common);
        if (common.size() < 3)
            return Double.NEGATIVE_INFINITY;
        // is this a zero collision pair?
        Set<String> ts1 = treeToTaxa.get(pair.t1);
        Set<String> ts2 = treeToTaxa.get(pair.t2);
        double score = taxaCache.size();
        if (ts1.containsAll(ts2)){
            score += taxaCache.size() + ts1.size();//todo better would be the number of all taxa...
        }else if (ts2.containsAll(ts1)){
            score +=  taxaCache.size() + ts2.size();
        }
        score -= (ts1.size() - common.size()) + (ts2.size() - common.size());
        score += (double)common.size()/(double)taxaCache.size();

        return score;
    }

}
