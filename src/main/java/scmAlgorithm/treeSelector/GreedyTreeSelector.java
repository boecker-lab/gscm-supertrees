package scmAlgorithm.treeSelector;

import epos.model.tree.Tree;
import gnu.trove.map.hash.THashMap;

import java.util.Collection;
import java.util.PriorityQueue;

/**
 * Created by fleisch on 14.10.15.
 */
public class GreedyTreeSelector extends DefaultGreedyTreeSelector<THashMap<Tree, PriorityQueue<TreePair>>, PriorityQueue<TreePair>> {

    public GreedyTreeSelector(TreeScorer scorer, ConsensusMethod method, boolean init, Tree... trees) {
        super(scorer, method, init, trees);
    }

    public GreedyTreeSelector(TreeScorer scorer, boolean init, Tree... trees) {
        super(scorer, init, trees);
    }

    public GreedyTreeSelector(TreeScorer scorer, Tree... trees) {
        super(scorer, trees);
    }

    public GreedyTreeSelector(TreeScorer scorer, ConsensusMethod method, Tree... trees) {
        super(scorer, method, trees);
    }

    public GreedyTreeSelector(TreeScorer scorer, Collection<Tree> treeCollection) {
        super(scorer, treeCollection);
    }

    @Override
    THashMap<Tree, PriorityQueue<TreePair>> getTreeToPairsInstance(int size) {
        return new THashMap<>(size);
    }


    @Override
    PriorityQueue<TreePair> getTreePairCollectionInstance(int size) {
        return new PriorityQueue<>(size);
    }

    //find best pair (O(nlog(n)))
    @Override
    TreePair getMax() {
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