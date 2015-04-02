package scmAlgorithm.treeSelector;

import epos.model.tree.Tree;
import scmAlgorithm.treeScorer.TreeScorer;

import java.util.*;

/**
 * Created by fleisch on 06.02.15.
 */
public abstract class TreeSelector {
    protected TreeScorer scorer;

    protected TreeSelector(TreeScorer scorer) {
        this.scorer = scorer;
    }

    public abstract TreePair pollTreePair();
    public abstract boolean addTree(Tree tree);
    public abstract int getNumberOfTrees();
    public abstract void init(Tree[] trees);

    public TreeScorer getScorer() {
        return scorer;
    }

    public void setScorer(TreeScorer scorer) {
        this.scorer = scorer;
    }

    public static abstract class DefaultGreedyTreeSelector<M extends Map<Tree, S>, S extends Collection<TreePair>> extends TreeSelector {
        protected final M treeToPairs;

        protected DefaultGreedyTreeSelector(TreeScorer scorer, boolean init,  Tree... trees) {
            super(scorer);
            this.treeToPairs = getTreeToPairsInstance(trees.length - 1);;
            if (init)
                init(trees);
        }

        protected DefaultGreedyTreeSelector(TreeScorer scorer, Tree... trees) {
            super(scorer);
            this.treeToPairs = getTreeToPairsInstance(trees.length - 1);
            init(trees);
        }

        protected DefaultGreedyTreeSelector(TreeScorer scorer, Collection<Tree> treeCollection) {
            this(scorer, treeCollection.toArray(new Tree[treeCollection.size()]));
        }


        // only one time called
        @Override
        public void init(Tree[] trees) {
            scorer.clear(new HashSet<>(Arrays.asList(trees)));//todo not very elegant
            treeToPairs.clear();

            for (Tree tree : trees) {
                treeToPairs.put(tree, getTreePairCollectionInstance(trees.length - 1));
            }

            for (int i = 0; i < trees.length - 1; i++) {
                Tree first = trees[i];
                for (int j = i + 1; j < trees.length; j++) {
                    Tree second = trees[j];
                    TreePair pair = new TreePair(first, second, scorer);
                    if (pair != null && pair.score > Double.NEGATIVE_INFINITY) {
                        addPair(first, pair);
                        addPair(second, pair);

                    }
                }
            }
        }

        //polls treepair from data structure
        public TreePair pollTreePair() {
            if (treeToPairs.size() > 1) {
                TreePair tp = getMax();
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
//        for (Map.Entry<Tree, S> entry : treeToPairs.entrySet()) {
            for (Tree old : treeToPairs.keySet()) {
//            Tree old = entry.getKey();
                TreePair pair = new TreePair(tree, old, scorer);

                if (pair != null && pair.score > Double.NEGATIVE_INFINITY) { //null pair have no overlap
                    pairsToAdd.add(pair);//add to own list O(log(n))
                    addPair(old, pair);//resfresh O(log(n)
//                entry.getValue().add(pair); //resfresh O(log(n))
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
            removePair(t1,pair);
            removePair(t2,pair);
            S s1 = treeToPairs.remove(t1); //O(1)
            S s2 = treeToPairs.remove(t2); //O(1)
//            s1.remove(pair);
//            s2.remove(pair);
            //O(n)
            for (TreePair treePair : s1) {
                removePair(treePair.getPartner(t1), treePair);//O(log(n))
            }
            //O(n)
            for (TreePair treePair : s2) {
                removePair(treePair.getPartner(t2), treePair);//O(log(n))
            }
        }

        protected abstract M getTreeToPairsInstance(int size);

        protected abstract S getTreePairCollectionInstance(int size);

        protected boolean addPair(Tree t, TreePair p) {
            return treeToPairs.get(t).add(p);
        }

        protected boolean removePair(Tree t, TreePair p) {
            return treeToPairs.get(t).remove(p);
        }

        @Override
        public int getNumberOfTrees() {
            return treeToPairs.size();
        }

        protected abstract TreePair getMax();


        public Tree[] getInputCopy() {
            List<Tree> c = new ArrayList();
            int i = 0;
            for (Tree tree : treeToPairs.keySet()) {
                if (tree != null)
                    c.add(tree.cloneTree());

            }
            return c.toArray(new Tree[c.size()]);
        }
    }
}
