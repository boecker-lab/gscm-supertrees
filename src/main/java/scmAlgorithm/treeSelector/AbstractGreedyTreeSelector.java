package scmAlgorithm.treeSelector;

import epos.model.tree.Tree;
import gnu.trove.map.hash.THashMap;
import scmAlgorithm.treeScorer.TreeScorer;

import java.util.*;

/**
 * Created by fleisch on 05.02.15.
 */
public abstract class AbstractGreedyTreeSelector<M extends Map<Tree, S>, S extends Collection<TreePair>> extends TreeSelector {
    protected final M treeToPairs;



    public AbstractGreedyTreeSelector(TreeScorer scorer, Tree... trees) {
        super(scorer);
        treeToPairs = getTreeToPairsInstance(trees.length - 1);
        init(trees);
    }

    public AbstractGreedyTreeSelector(TreeScorer scorer, Collection<Tree> treeCollection) {
        this(scorer, treeCollection.toArray(new Tree[treeCollection.size()]));
    }


    // only one time called
    private void init(Tree[] trees) {
        for (Tree tree : trees) {
            treeToPairs.put(tree, getTreePairCollectionInstance(trees.length - 1));
        }

        for (int i = 0; i < trees.length - 1; i++) {
            Tree first = trees[i];
            for (int j = i + 1; j < trees.length; j++) {
                Tree second = trees[j];
                TreePair pair = new TreePair(first, second,scorer);
                if (pair != null) {
                    treeToPairs.get(first).add(pair);
                    treeToPairs.get(second).add(pair);
                }
            }
        }
    }

    //polls treepair from data structure
    public TreePair pollTreePair() {
        if (treeToPairs.size() > 1) {
            TreePair tp = findGlobalMax();
            removeTreePair(tp);
            return tp;
        }
        return null;
    }

    ;

    // adds tree and its paires to the data structure (O(2nlog(n)))
    public boolean addTree(Tree tree) {
        if (treeToPairs.isEmpty())
            return false;
        S pairsToAdd = getTreePairCollectionInstance(treeToPairs.size());

        //iterate over trees (O(n)) to add to new list and refresh old entries
        for (Map.Entry<Tree, S> entry : treeToPairs.entrySet()) {
            Tree old = entry.getKey();
            TreePair pair = new TreePair(tree, old, scorer);

            if (pair != null) { //null pair have no overlap
                pairsToAdd.add(pair);//add to own list O(log(n))
                entry.getValue().add(pair); //resfresh O(log(n))
            }
        }
        //add new to map O(1)
        treeToPairs.put(tree, pairsToAdd);
        return true;
    }


    //(O(2nlog(n))
    private void removeTreePair(TreePair pair) {
        Tree t1 = pair.t1; //O(1)
        Tree t2 = pair.t2; //O(1)
        S s1 = treeToPairs.remove(t1); //O(1)
        S s2 = treeToPairs.remove(t2); //O(1)
        s1.remove(pair);
        s2.remove(pair);
        //O(n)
        for (TreePair treePair : s1) {
            treeToPairs.get(treePair.getPartner(t1))
                    .remove(treePair); //O(log(n))
        }
        //O(n)
        for (TreePair treePair : s2) {
            treeToPairs.get(treePair.getPartner(t2))
                    .remove(treePair); //O(log(n))
        }
    }

    protected abstract M getTreeToPairsInstance(int size);

    protected abstract S getTreePairCollectionInstance(int size);

    protected abstract TreePair getMax(S treePairs);

    protected abstract TreePair findGlobalMax();

    public Tree[] getInputCopy(){
        List<Tree> c = new ArrayList();
        int i = 0;
        for (Tree tree : treeToPairs.keySet()) {
            if (tree != null)
                c.add(tree.cloneTree());

        }
        return c.toArray(new Tree[c.size()]);
    }


//#############################################################################
//########################## IMPLEMENTATIONS ##################################
//#############################################################################


    public static class GreedyTreeSelector extends AbstractGreedyTreeSelector<THashMap<Tree, PriorityQueue<TreePair>>, PriorityQueue<TreePair>> {

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

        @Override
        protected TreePair getMax(PriorityQueue<TreePair> treePairs) {
            return treePairs.peek();
        }

        //find best pair (O(nlog(n)))
        @Override
        protected TreePair findGlobalMax() {
            TreePair best = TreePair.MIN_VALUE;
            //iteration in O(n)
            for (PriorityQueue<TreePair> treePairs : treeToPairs.values()) {
                TreePair max = getMax(treePairs); //get local best in O(log(n))
                if (max.compareTo(best) < 0)
                    best = max;
            }
            return best;
        }


    }
}
