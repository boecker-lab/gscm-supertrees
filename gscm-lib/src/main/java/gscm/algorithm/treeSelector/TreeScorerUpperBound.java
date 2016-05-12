package gscm.algorithm.treeSelector;/**
 * Created by Markus Fleischauer (markus.fleischauer@gmail.com)
 * as part of the gscm
 * 27.04.16.
 */


/**
 * @author Markus Fleischauer (markus.fleischauer@gmail.com)
 */
public abstract class TreeScorerUpperBound extends TreeScorer {

    public TreeScorerUpperBound() {
        super();
    }

    public TreeScorerUpperBound(boolean syncedCache) {
        super(syncedCache);
    }

    protected abstract double calculateScoreUpperBound(TreePair pair);

    public TreePair scoreUpperBoundTreePair(TreePair pair) {
        if (calculateAndAddCommonLeafes(pair) > 2) {
            pair.upperBound = calculateScoreUpperBound(pair);
        }else{
            pair.score = Double.NEGATIVE_INFINITY;
            pair.upperBound = Double.NEGATIVE_INFINITY;
        }
        return pair;
    }

    public static class UniqueCladesRemainingNumberScorer extends TreeScorerUpperBound {
        public UniqueCladesRemainingNumberScorer() {
            super();
        }

        public UniqueCladesRemainingNumberScorer(boolean syncedCache) {
            super(syncedCache);
        }

        @Override
        protected double calculateScore(TreePair pair) {
            pair.calculateConsensus();
            return getNumRemainingUniqueClades(pair);
        }

        @Override
        protected double calculateTieBreakScore(TreePair pair) {
            return getNumOfConsensusVertices(pair) - getNumOfConsensusTaxa(pair);
        }

        @Override
        protected double calculateScoreUpperBound(TreePair pair) {
//            return getNumUniqueClades(pair);
            return getNumberOfUniqueTaxa(pair);
        }
    }

}
