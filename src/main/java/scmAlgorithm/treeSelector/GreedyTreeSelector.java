package scmAlgorithm.treeSelector;

import epos.model.tree.Tree;
import gnu.trove.map.hash.THashMap;
import scmAlgorithm.treeScorer.TreeScorer;

import java.util.PriorityQueue;

/**
 * Created by fleisch on 23.03.15.
 */
public class GreedyTreeSelector extends TreeSelector.DefaultGreedyTreeSelector<THashMap<Tree, PriorityQueue<TreePair>>, PriorityQueue<TreePair>> {

    protected GreedyTreeSelector(TreeScorer scorer, boolean init, Tree... trees) {
        super(scorer, init, trees);
    }

    public GreedyTreeSelector(TreeScorer scorer, Tree... trees) {
        super(scorer, trees);
    }

    @Override
    protected THashMap<Tree, PriorityQueue<TreePair>> getTreeToPairsInstance(int size) {
        return new THashMap<>(size);
    }


    @Override
    protected PriorityQueue<TreePair> getTreePairCollectionInstance(int size) {
        return new PriorityQueue<>(size);
    }

    //find best pair (O(nlog(n)))
    @Override
    protected TreePair getMax() {
        TreePair best = TreePair.MIN_VALUE;
        //iteration in O(n)
        for (PriorityQueue<TreePair> treePairs : treeToPairs.values()) {
            TreePair max = treePairs.peek(); //get local best in O(log(n))
            if (max.compareTo(best) < 0)
                best = max;
        }
        return best;
    }
}
