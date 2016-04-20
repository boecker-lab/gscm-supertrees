package scm.algorithm.treeSelector;

import gnu.trove.map.hash.THashMap;
import phyloTree.model.tree.Tree;

import java.util.PriorityQueue;

/**
 * Created by fleisch on 14.10.15.
 */
public class GreedyTreeSelector extends MapBasedGreedyTreeSelector<THashMap<Tree, PriorityQueue<TreePair>>, PriorityQueue<TreePair>> {
    public static final TreeSelectorFactory<GreedyTreeSelector> FACTORY = () -> {
        GreedyTreeSelector s =  new GreedyTreeSelector();
        TreeSelectorFactory.selectors.add(s);
        return s;
    };
    private GreedyTreeSelector() {}

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
            if (max.compareTo(best) < 0) //Attention the comparator is reverse!
                best = max;
        }
        return best;
    }
}