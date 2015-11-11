package scm.algorithm.treeSelector;

import epos.model.tree.Tree;
import gnu.trove.map.hash.THashMap;

import java.util.PriorityQueue;

/**
 * Created by fleisch on 14.10.15.
 */
public class GreedyTreeSelector extends MapBasedGreedyTreeSelector<THashMap<Tree, PriorityQueue<TreePair>>, PriorityQueue<TreePair>> {
    private static final GreedyTreeSelectorFactory FACTORY = new GreedyTreeSelectorFactory();

    public GreedyTreeSelector(ConsensusMethod method) {
        super(method);
    }

    public GreedyTreeSelector() {
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


    public static GreedyTreeSelectorFactory getFactory() {
        return FACTORY;
    }

    private static class GreedyTreeSelectorFactory implements TreeSelectorFactory<GreedyTreeSelector>{
        @Override
        public GreedyTreeSelector newTreeSelectorInstance() {
            return new GreedyTreeSelector();
        }
    }
}