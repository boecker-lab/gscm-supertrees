package scmAlgorithm.treeSelector;

import epos.model.tree.Tree;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Created by fleisch on 06.02.15.
 */
public abstract class DefaultGreedyTreeSelector<M extends Map<Tree, S>, S extends Collection<TreePair>> implements TreeSelector {
    private TreeScorer scorer;
    private ConsensusMethod method;
    final M treeToPairs;

    DefaultGreedyTreeSelector(TreeScorer scorer, ConsensusMethod method, boolean init, Tree... trees) {
        this.scorer = scorer;
        this.method = method;
        this.treeToPairs = getTreeToPairsInstance(trees.length - 1);
        if (init)
            init(trees);
    }

    DefaultGreedyTreeSelector(TreeScorer scorer, boolean init, Tree... trees) {
        this(scorer, ConsensusMethod.STRICT, init, trees);
    }

    DefaultGreedyTreeSelector(TreeScorer scorer, Tree... trees) {
        this(scorer, ConsensusMethod.STRICT, trees);
    }

    DefaultGreedyTreeSelector(TreeScorer scorer, ConsensusMethod method, Tree... trees) {
        this(scorer, method, true, trees);
    }

    DefaultGreedyTreeSelector(TreeScorer scorer, Collection<Tree> treeCollection) {
        this(scorer, treeCollection.toArray(new Tree[treeCollection.size()]));
    }


    // only one time called
    @Override
    public void init(Tree[] trees) {
        Set<Tree> ts = new HashSet<>(trees.length);
        Collections.addAll(ts, trees);
        scorer.clearCache(ts);

        treeToPairs.clear();

        for (Tree tree : trees) {
            treeToPairs.put(tree, getTreePairCollectionInstance(trees.length - 1));
        }

        long time = System.currentTimeMillis();

        //parralel scoring
        //todo use nice executaer service
        System.out.println("Calculating initial scores of tree pairs (multi threaded)...");
        List<TreePair> pairsToAdd = IntStream.range(0, trees.length - 1).parallel()
                .boxed()
                .flatMap(i -> IntStream.range(i + 1, trees.length)
                        .mapToObj(j -> new TreePair(trees[i], trees[j], scorer, newConsensusCalculatorInstance(method))))
                .collect(Collectors.toList());


        //sequencial adding
        for (TreePair pair : pairsToAdd) {
            if (pair != null && pair.score > Double.NEGATIVE_INFINITY) {
                addPair(pair.t1, pair);
                addPair(pair.t2, pair);
            }
        }
        double runtime = (double) (System.currentTimeMillis() - time) / 1000d;
        System.out.println("...Done in: " + runtime + "s");
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

    // adds tree and its paires to the data structure (O(2nlog(n)))
    public boolean addTree(Tree tree) {
        if (treeToPairs.isEmpty())
            return false;
        S pairsOfnuTree = getTreePairCollectionInstance(treeToPairs.size());

        //iterate over trees (O(n)) to add to new list and refresh old entries
        //todo use nice executaer service
        List<TreePair> pairsToAdd = treeToPairs.keySet().parallelStream()
                .map(old -> new TreePair(tree, old, scorer, newConsensusCalculatorInstance(method)))
                .collect(Collectors.toList());

        for (TreePair pair : pairsToAdd) {
            if (pair != null && pair.score > Double.NEGATIVE_INFINITY) { //null pair have no overlap
                pairsOfnuTree.add(pair);//add to own list O(log(n))
                addPair(pair.t2, pair);//resfresh O(log(n)
//                entry.getValue().add(pair); //resfresh O(log(n))
            }
        }
        if (pairsOfnuTree.isEmpty())
            throw new IllegalArgumentException("Input tree have insufficient Overlap to calculate a supertree");
        //add new to map O(1)
        treeToPairs.put(tree, pairsOfnuTree);
        return true;
    }


    //(O(2nlog(n))
    private void removeTreePair(TreePair pair) {
        Tree t1 = pair.t1; //O(1)
        Tree t2 = pair.t2; //O(1)
        removePair(t1, pair);
        removePair(t2, pair);
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

    @Override
    public TreeScorer getScorer() {
        return scorer;
    }

    @Override
    public void setScorer(TreeScorer scorer) {
        this.scorer = scorer;
    }

    //abstract classes
    abstract M getTreeToPairsInstance(int size);

    abstract S getTreePairCollectionInstance(int size);

    boolean addPair(Tree t, TreePair p) {
        return treeToPairs.get(t).add(p);
    }

    boolean removePair(Tree t, TreePair p) {
        return treeToPairs.get(t).remove(p);
    }

    @Override
    public int getNumberOfTrees() {
        return treeToPairs.size();
    }

    abstract TreePair getMax();
}

