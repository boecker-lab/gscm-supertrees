package scm.algorithm.treeSelector;

/**
 * Created by fleisch on 23.03.15.
 */

import gnu.trove.map.hash.THashMap;
import gnu.trove.set.hash.THashSet;
import phyloTree.model.tree.Tree;

import java.util.Random;


public class RandomizedGreedyTreeSelector extends MapBasedGreedyTreeSelector<THashMap<Tree, THashSet<TreePair>>, THashSet<TreePair>>{
    private static final RandomizedGreedyTreeSelectorFactory FACTORY = new RandomizedGreedyTreeSelectorFactory();
    private static final Random RAND = new Random();

    private double sumOfScores = 0d;
    private THashSet<TreePair> pairs;

    private double[] indices;
    private TreePair[] pairToIndex;


    public RandomizedGreedyTreeSelector(ConsensusMethod method) {
        super(method);
    }

    public RandomizedGreedyTreeSelector() {
    }

    @Override
    TreePair getMax() {
        if (treeToPairs.isEmpty())
            return TreePair.MIN_VALUE;
        return peekRandomPair();
    }

    @Override
    THashMap<Tree, THashSet<TreePair>> getTreeToPairsInstance(int size) {
        return new THashMap<Tree, THashSet<TreePair>>(size);
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
    void addTreePair(Tree t, TreePair p) {
        super.addTreePair(t,p);
        addTreePairToRandomStructure(p);
    }


    @Override
    void removePair(Tree tree,TreePair pair) {
        super.removePair(tree, pair);
        removePairFromRandomStructure(pair);
    }

    private void removePairFromRandomStructure(TreePair pair) {
        if (pairs.remove(pair)) {
            sumOfScores -= pair.score;
            clearCache();
        }
    }


    private void clearCache() {
        indices = null;
        pairToIndex = null;
    }


    private void initRandomStructures(){
        indices = new double[pairs.size()];
        pairToIndex = new TreePair[pairs.size()];//todo, do fany index stuff insteadd of this ugly set thinky
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

    private TreePair peekRandomPair() {
        if (indices == null || pairToIndex == null){
            initRandomStructures();
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

    @Override
    public void setInputTrees(Tree... inputTrees) {
        super.setInputTrees(inputTrees);
        pairs = new THashSet<>(inputTrees.length * (inputTrees.length-1)/2);
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
