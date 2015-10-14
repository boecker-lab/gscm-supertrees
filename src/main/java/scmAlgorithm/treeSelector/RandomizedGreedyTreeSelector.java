package scmAlgorithm.treeSelector;

/**
 * Created by fleisch on 23.03.15.
 */

import epos.model.tree.Tree;
import gnu.trove.set.hash.THashSet;

import java.util.Random;

public class RandomizedGreedyTreeSelector extends GreedyTreeSelector {
    private final RandomTreePairPicker selector;

    //todo implementation forn non strict versions???
    public RandomizedGreedyTreeSelector(TreeScorer scorer, Tree... trees) {
        super(scorer, false, trees);
        selector = new RandomTreePairPicker();
        init(trees);
    }

    public RandomizedGreedyTreeSelector(TreeScorer scorer, boolean init, Tree... trees) {
        super(scorer, false, trees);
        selector = new RandomTreePairPicker();
        if (init)
            init(trees);
    }

    @Override
    protected boolean addPair(Tree t, TreePair p) {
        selector.addPair(p);
        return super.addPair(t, p);
    }

    @Override
    protected boolean removePair(Tree t, TreePair p) {
        selector.removePair(p);
        return super.removePair(t, p);
    }

    //find best pair (O(nlog(n)))
    @Override
    protected TreePair getMax() {
        return selector.peekRandomPair();
    }


    private class RandomTreePairPicker {
        final Random rand = new Random();
        double sumOfScores = 0d;
        final THashSet<TreePair> pairs = new THashSet<>();

        double[] indices;
        TreePair[] pairToIndex;
//        TreePair pair = null;


        void addPair(TreePair pair) {
            if (pairs.add(pair)) {
                sumOfScores += pair.score;
                clearCache();
            }

        }

        TreePair removePair(TreePair pair) {
            if (pairs.remove(pair)) {
                sumOfScores -= pair.score;
                clearCache();
            }
            return pair;
        }

        private void clearCache() {
            indices = null;
            pairToIndex = null;
//            pair = null;
        }

        private TreePair findNewRandomPair() {
            if (indices == null || pairToIndex == null) {
                indices = new double[pairs.size()];
                pairToIndex = new TreePair[pairs.size()];
                int index = 0;
                double pre = 0d;
                for (TreePair pair : pairs) {
                    pairToIndex[index] = pair;
                    //this is for compatibility with negative scores
                    if (sumOfScores > 0d) {
                        pre += pair.score / sumOfScores;
                    } else {
                        pre += 1d - (pair.score / sumOfScores);
                    }
                    indices[index] = pre;
                    index++;
                }
            }


            double r = rand.nextDouble();
            int start = 0;
            int end = indices.length - 1;
            TreePair pair = pairToIndex[0];

            while (start != end) {

                int index = start + (((end - start)) / 2);

                if (r > indices[index]) {
                    start = index + 1;
                    pair = pairToIndex[start];
                } else {
                    end = index;
                    pair = pairToIndex[end];
                }
            }
            return pair;
        }

        TreePair peekRandomPair() {
            return findNewRandomPair();
        }
    }

}
