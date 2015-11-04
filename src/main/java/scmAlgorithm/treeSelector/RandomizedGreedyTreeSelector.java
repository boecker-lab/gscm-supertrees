package scmAlgorithm.treeSelector;

/**
 * Created by fleisch on 23.03.15.
 */

import epos.model.tree.Tree;
import gnu.trove.map.hash.THashMap;
import gnu.trove.set.hash.THashSet;

import java.util.*;


public class RandomizedGreedyTreeSelector extends MapBasedGreedyTreeSelector<THashMap<Tree, THashSet<TreePair>>, THashSet<TreePair>>{
    private static final RandomizedGreedyTreeSelectorFactory FACTORY = new RandomizedGreedyTreeSelectorFactory();
    private static final Random RAND = new Random();

    private double sumOfScores = 0d;
    private THashSet<TreePair> pairs = new THashSet<>();

    private double[] indices;
    private TreePair[] pairToIndex;


    public RandomizedGreedyTreeSelector(ConsensusMethod method) {
        super(method);
    }

    public RandomizedGreedyTreeSelector() {
    }

    // only one time called
    @Override
    public void init() {
        super.init();
        pairs = new THashSet<>(inputTrees.length * (inputTrees.length-1)/2);
    }

    @Override
    TreePair getMax() {
        return peekRandomPair();
    }

    @Override
    void addTreePair(Tree t, TreePair p) {
        super.addTreePair(t,p);
        addTreePairToRandomStructure(p);
    }

    @Override
    THashMap<Tree, THashSet<TreePair>> getTreeToPairsInstance(int size) {
        return new THashMap<>(size);
    }

    @Override
    THashSet<TreePair> getTreePairCollectionInstance(int size) {
        return new THashSet<>(size);
    }

    private void addTreePairToRandomStructure(TreePair pair) {
        if (pairs.add(pair)) {
            sumOfScores += pair.score;
            clearCache();
        }
    }

    @Override
    void removeTreePair(TreePair pair) {
        super.removeTreePair(pair);
        removeTreePairFromRandomStructure(pair);
    }

    private void removeTreePairFromRandomStructure(TreePair pair) {
        if (pairs.remove(pair)) {
            sumOfScores -= pair.score;
            clearCache();
        }
    }


    private void clearCache() {
        indices = null;
        pairToIndex = null;
    }


    private TreePair peekRandomPair() {
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


        double r = RAND.nextDouble();
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

    public static RandomizedGreedyTreeSelectorFactory getFactory() {
        return FACTORY;
    }

    private static class RandomizedGreedyTreeSelectorFactory implements TreeSelectorFactory<RandomizedGreedyTreeSelector>{
        @Override
        public RandomizedGreedyTreeSelector newTreeSelectorInstance() {
            return new RandomizedGreedyTreeSelector();
        }
    }
}
